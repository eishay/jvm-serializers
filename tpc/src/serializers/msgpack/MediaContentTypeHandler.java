package serializers.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.NotNullableTemplate;
import org.msgpack.template.Template;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public final class MediaContentTypeHandler extends TypeHandler<MediaContent> {
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
}