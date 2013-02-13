
package serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;
import data.media.MediaContentCustom;
import net.sockali.obser.ObserEncoding;
import net.sockali.obser.ObserFactory;

public class Obser {
	public static void register (TestGroups groups) {
		register(groups.media, JavaBuiltIn.mediaTransformer);
	}

	private static <T, S> void register (TestGroup group, Transformer transformer) {
		group.add(transformer, new BasicSerializer<S>());
		group.add(transformer, new CompactSerializer<S>());
		group.add(transformer, new CustomSerializer());
		group.add(transformer, new CustomCompactSerializer());
	}


	// ------------------------------------------------------------
	// Serializers

	/** This is the most basic Kryo usage. Just register the classes and go. */
	public static class BasicSerializer<T> extends Serializer<T> {
		final net.sockali.obser.Obser obser;
//		private final byte[] buffer = new byte[1024*1024];
		private ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
		
		public BasicSerializer () {
			this.obser = ObserFactory.createObser(ObserEncoding.nativeEncoding());
			obser.registerClass(data.media.MediaContentCustom.class);
			obser.registerClass(MediaContent.class);
			obser.registerClass(Media.class);
			obser.registerClass(Media.Player.class);
			obser.registerClass(Image.class);
			obser.registerClass(Image.Size.class);
			obser.registerClass(ArrayList.class);
			obser.registerClass(MediaContent[].class);
			obser.registerClass(MediaContentCustom[].class);
		}

		public T deserialize (byte[] array) {
			buffer.position(0);
			buffer.put(array);
			
			return obser.deserialize(buffer, 0);
		}

		public byte[] serialize (Object content) {
				buffer.position(0);
				int pos = obser.serialize(content, buffer, 0);
				byte[] ret = new byte[pos];
				buffer.get(ret);
				return ret;
		}

		public void serializeItems (T[] items, OutputStream outStream) throws Exception {
			obser.serialize(items, outStream);
		}

		public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
			return obser.deserialize(inStream);
		}

		public String getName () {
			return "obser";
		}
	}

	/** This shows how to configure individual Kryo serializersto reduce the serialized bytes. */
	public static class CompactSerializer<T> extends BasicSerializer<T> {
		public CompactSerializer () {
			super();
			obser.setEncoding(ObserEncoding.nativeCompactEncoding());
		}

		public String getName () {
			return "obser-compact";
		}
	}
	
	public static class CustomSerializer extends BasicSerializer<MediaContent> {
		@Override
		public byte[] serialize(MediaContent content) {
			MediaContentCustom mcc = new MediaContentCustom(content);
			return super.serialize(mcc);
		}
		
		@Override
		public MediaContent deserialize(byte[] array) {
			MediaContentCustom mcc = (MediaContentCustom) ((Object) super.deserialize(array));
			return mcc.getContent();
		}
		
		@Override
		public void serializeItems(MediaContent[] items, OutputStream outStream) throws Exception {
			MediaContentCustom[] data = new MediaContentCustom[items.length];
			for (int i=0; i<items.length; i++)
				data[i] = new MediaContentCustom(items[i]);
			super.serializeItems((MediaContent[]) (Object)data, outStream);
		}
		
		@Override
		public MediaContent[] deserializeItems(InputStream inStream, int numberOfItems) throws IOException {
			MediaContentCustom[] data = (MediaContentCustom[]) (Object) super.deserializeItems(inStream, numberOfItems);
			MediaContent[] items = new MediaContent[data.length];
			for (int i=0; i<data.length; i++)
				items[i] = data[i].getContent();
			
			return items;
		}
		public String getName () {
			return "obser-manual";
		}
	}

	public static class CustomCompactSerializer extends CustomSerializer {
		
		public CustomCompactSerializer() {
			obser.setEncoding(ObserEncoding.nativeCompactEncoding());
		}
		
		public String getName () {
			return "obser-manual-compact";
		}
	}
}
