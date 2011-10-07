package serializers;

import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.ListTemplate;
import org.msgpack.template.NotNullableTemplate;
import org.msgpack.template.OrdinalEnumTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class MsgPack
{
    public static void register(TestGroups groups) {
	register(groups.media, JavaBuiltIn.mediaTransformer, new MediaContentTypeHandler());
    }

    private static <T,S> void register(TestGroup<T> group, Transformer<T,S> transformer, TypeHandler<S> handler) {
	group.add(transformer, new BasicSerializer<S>(handler));
	//group.add(transformer, new ManualSerializer<S>(handler));
    }

    public static abstract class AbstractSerializer<T> extends Serializer<T> {
	protected Class<T> type;
	protected String name;
	protected MessagePack msgpack;
	private BufferPacker packer;
	private BufferUnpacker unpacker;

	protected AbstractSerializer(TypeHandler<T> handler, MessagePack msgpack) {
	    type = handler.type;
	    this.msgpack = msgpack;
	    packer = this.msgpack.createBufferPacker(2048);
	    unpacker = this.msgpack.createBufferUnpacker();
	}

	@Override
	public byte[] serialize(T content) throws Exception {
	    packer.write(content);
	    byte[] array = packer.toByteArray();
	    packer.clear();
	    return array;
	}

	@Override
	public T deserialize(byte[] array) throws Exception {
	    unpacker.wrap(array);
	    return unpacker.read(type);
	}

	@Override
	public String getName() {
	    return name;
	}
    }

    public static final class BasicSerializer<T> extends AbstractSerializer<T> {
	public BasicSerializer(TypeHandler<T> handler) {
	    super(handler, new MessagePack());
	    handler.register(msgpack);
	    name = "msgpack";
	}
    }

    public static final class ManualSerializer<T> extends AbstractSerializer<T> {
	public ManualSerializer(TypeHandler<T> handler) {
	    super(handler, new MessagePack());
	    handler.registerManually(msgpack);
	    name = "msgpack-manual";
	}
    }

    public static abstract class TypeHandler<T> {
	public final Class<T> type;

	protected TypeHandler(Class<T> type) {
	    this.type = type;
	}

	public abstract void register(final MessagePack msgpack);

	public abstract void registerManually(final MessagePack msgpack);
    }

    public static final class MediaContentTypeHandler extends TypeHandler<MediaContent> {
	protected MediaContentTypeHandler() {
	    super(MediaContent.class);
	}

	@Override
	public void register(MessagePack msgpack) {
	    msgpack.register(Image.Size.class);
	    msgpack.register(Image.class);
	    msgpack.register(Media.Player.class);
	    msgpack.register(Media.class);
	    msgpack.register(MediaContent.class);
	}

	static final Template<MediaContent> mediaContentTemplate = new NotNullableTemplate<MediaContent>(MediaContentTemplate.INSTANCE);

	@Override
	public void registerManually(MessagePack msgpack) {
	    msgpack.register(MediaContent.class, mediaContentTemplate);
	}

	static final class ImageSizeTemplate extends OrdinalEnumTemplate<Image.Size> {
	    static final Template<Image.Size> INSTANCE = new ImageSizeTemplate(Image.Size.class);

	    public ImageSizeTemplate(Class<Image.Size> targetClass) {
		super(targetClass);
	    }
	}

	static final class ImageTemplate extends AbstractTemplate<Image> {
	    static final Template<Image> INSTANCE = new ImageTemplate();
	    static final Template<Image.Size> imageSizeTemplate =
		new NotNullableTemplate<Image.Size>(ImageSizeTemplate.INSTANCE);

	    @Override
	    public void write(Packer packer, Image v, boolean required) throws IOException {
		packer.writeArrayBegin(5);
		Templates.TString.write(packer, v.uri, true);
		Templates.TString.write(packer, v.title, false);
		Templates.TInteger.write(packer, v.width, true);
		Templates.TInteger.write(packer, v.height, true);
		imageSizeTemplate.write(packer, v.size);
		packer.writeArrayEnd();
	    }

	    @Override
	    public Image read(Unpacker unpacker, Image to, boolean required) throws IOException {
		unpacker.readArrayBegin();
		to = new Image(
			Templates.TString.read(unpacker, null, true),
			Templates.TString.read(unpacker, null, false),
			Templates.TInteger.read(unpacker, null, true),
			Templates.TInteger.read(unpacker, null, true),
			imageSizeTemplate.read(unpacker, null, true)
		);
		unpacker.readArrayEnd();
		return to;
	    }
	}

	static final class MediaPlayerTemplate extends OrdinalEnumTemplate<Media.Player> {
	    static final Template<Media.Player> INSTANCE = new MediaPlayerTemplate(Media.Player.class);

	    public MediaPlayerTemplate(Class<Media.Player> targetClass) {
		super(targetClass);
	    }
	}

	static final class MediaTemplate extends AbstractTemplate<Media> {
	    static final Template<Media> INSTANCE = new MediaTemplate();
	    static final Template<Media.Player> mediaPlayerTemplate =
		new NotNullableTemplate<Media.Player>(MediaPlayerTemplate.INSTANCE);
	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    static final Template<List<String>> personsTemplate = 
		new NotNullableTemplate<List<String>>(new ListTemplate(
			new NotNullableTemplate<String>(StringTemplate.getInstance())));

	    @Override
	    public void write(Packer packer, Media v, boolean required) throws IOException {
		packer.writeArrayBegin(12);
		Templates.TString.write(packer, v.uri, true);
		Templates.TString.write(packer, v.title, false);
		Templates.TInteger.write(packer, v.width, true);
		Templates.TInteger.write(packer, v.height, true);
		Templates.TString.write(packer, v.format, true);
		Templates.TLong.write(packer, v.duration, true);
		Templates.TLong.write(packer, v.size, true);
		Templates.TInteger.write(packer, v.bitrate, true);
		Templates.TBoolean.write(packer, v.hasBitrate, true);
		personsTemplate.write(packer, v.persons, true);
		mediaPlayerTemplate.write(packer, v.player, true);
		Templates.TString.write(packer, v.copyright, false);
		packer.writeArrayEnd();
	    }

	    @Override
	    public Media read(Unpacker unpacker, Media to, boolean required) throws IOException {
		unpacker.readArrayBegin();
		to = new Media(
			Templates.TString.read(unpacker, null, true),
			Templates.TString.read(unpacker, null, false),
			Templates.TInteger.read(unpacker, null, true),
			Templates.TInteger.read(unpacker, null, true),
			Templates.TString.read(unpacker, null, true),
			Templates.TLong.read(unpacker, null, true),
			Templates.TLong.read(unpacker, null, true),
			Templates.TInteger.read(unpacker, null, true),
			Templates.TBoolean.read(unpacker, null, true),
			personsTemplate.read(unpacker, null, true),
			mediaPlayerTemplate.read(unpacker, null, true),
			Templates.TString.read(unpacker, null, false)
		);
		unpacker.readArrayEnd();
		return to;
	    }
	}

	static final class MediaContentTemplate extends AbstractTemplate<MediaContent> {
	    static final Template<MediaContent> INSTANCE = new MediaContentTemplate();
	    static final Template<Media> mediaTemplate = new NotNullableTemplate<Media>(MediaTemplate.INSTANCE);
	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    static final Template<List<Image>> imageListTemplate = 
		new NotNullableTemplate<List<Image>>(new ListTemplate(
			new NotNullableTemplate<Image>(ImageTemplate.INSTANCE)));

	    @Override
	    public void write(Packer packer, MediaContent v, boolean required) throws IOException {
		packer.writeArrayBegin(2);
		mediaTemplate.write(packer, v.media, true);
		imageListTemplate.write(packer, v.images, true);
		packer.writeArrayEnd();
	    }

	    @Override
	    public MediaContent read(Unpacker unpacker, MediaContent to, boolean required) throws IOException {
		unpacker.readArrayBegin();
		to = new MediaContent(
			mediaTemplate.read(unpacker, null, true),
			imageListTemplate.read(unpacker, null, true)
		);
		unpacker.readArrayEnd();
		return to;
	    }
	}
    }
}