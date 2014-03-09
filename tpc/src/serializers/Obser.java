
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

@SuppressWarnings("unchecked")
public class Obser {
	public static void register (TestGroups groups) {
		register(groups.media, JavaBuiltIn.mediaTransformer);
	}

	private static <T, S> void register (TestGroup<?> group, Transformer transformer) {
		group.add(transformer, new BasicSerializer<S>(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
		group.add(transformer, new CompactSerializer<S>(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.CLASSES_KNOWN,
                        ""
                )
        );
		group.add(transformer, new CustomSerializer(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                        ""
                )
        );
		group.add(transformer, new CustomCompactSerializer(),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                        ""
                )
        );
	}


	// ------------------------------------------------------------
	// Serializers

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

		@Override
		public T deserialize (byte[] array) {
			buffer.position(0);
			buffer.put(array);
			
			return (T) obser.deserialize(buffer, 0);
		}

          @Override
		public byte[] serialize (T content) {
				buffer.position(0);
				int pos = obser.serialize(content, buffer, 0);
				byte[] ret = new byte[pos];
				buffer.get(ret);
				return ret;
		}

          @Override
		public void serializeItems (T[] items, OutputStream outStream) throws Exception {
			obser.serialize(items, outStream);
		}

          @Override
		public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
			return obser.deserialize(inStream);
		}

          @Override
		public String getName () {
			return "obser";
		}
	}

	public static class CompactSerializer<T> extends BasicSerializer<T> {
		public CompactSerializer () {
			super();
			obser.setEncoding(ObserEncoding.nativeCompactEncoding());
		}

          @Override
		public String getName () {
			return "obser-compact";
		}
	}
	
	public static class CustomSerializer extends BasicSerializer<Object> {
		@Override
		public byte[] serialize(Object content) {
			MediaContentCustom mcc = new MediaContentCustom((MediaContent) content);
			return super.serialize(mcc);
		}
		
		@Override
		public MediaContent deserialize(byte[] array) {
			MediaContentCustom mcc = (MediaContentCustom) ((Object) super.deserialize(array));
			return mcc.getContent();
		}
		
		@Override
		public void serializeItems(Object[] items, OutputStream outStream) throws Exception {
			MediaContentCustom[] data = new MediaContentCustom[items.length];
			for (int i=0; i<items.length; i++)
				data[i] = new MediaContentCustom((MediaContent)items[i]);
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

		@Override
		public String getName () {
			return "obser-manual";
		}
	}

	public static class CustomCompactSerializer extends CustomSerializer {
		
		public CustomCompactSerializer() {
			obser.setEncoding(ObserEncoding.nativeCompactEncoding());
		}

		@Override
		public String getName () {
			return "obser-manual-compact";
		}
	}
}
