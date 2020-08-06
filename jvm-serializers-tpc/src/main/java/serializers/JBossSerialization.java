package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;
import org.jboss.serial.util.StringUtilBuffer;

import data.media.MediaContent;

public class JBossSerialization {

	public static void register(final TestGroups groups) {
        groups.media.add(
    		JavaBuiltIn.mediaTransformer,
    		new JBossSerializationSerializer<MediaContent>(MediaContent.class)
                ,
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
        );
    }

	private static final class JBossSerializationSerializer<T>
		extends Serializer<T> {

		private final Class<T> clz;

	    public JBossSerializationSerializer(final Class<T> c) {
	    	clz = c;
    	}

        @Override
		public String getName() {
        	return "jboss-serialization";
    	}

        @Override
		@SuppressWarnings("unchecked")
        public T deserialize(final byte[] array) throws Exception {
	        ByteArrayInputStream bais = new ByteArrayInputStream(array);
	        JBossObjectInputStream jois = new JBossObjectInputStream(
        		bais,
        		new StringUtilBuffer(BUFFER_SIZE, BUFFER_SIZE)
    		);
	        return (T) jois.readObject();
	    }

	    @Override
		public byte[] serialize(final T data) throws IOException {
	        ByteArrayOutputStream baos = outputStream(data);
	        JBossObjectOutputStream joos = new JBossObjectOutputStream(
        		baos,
        		new StringUtilBuffer(BUFFER_SIZE, BUFFER_SIZE)
    		);
	        joos.writeObject(data);
	        return baos.toByteArray();
	    }

        @Override
        public final void serializeItems(final T[] items, final OutputStream os)
			throws Exception {
        	JBossObjectOutputStream jous = new JBossObjectOutputStream(os);
            for (Object item : items) {
            	jous.writeObject(item);
            }
            jous.flush();
            jous.close();
        }

        @SuppressWarnings("unchecked")
        @Override
        public T[] deserializeItems(final InputStream in, final int numOfItems)
    		throws Exception {
        	JBossObjectInputStream jois = new JBossObjectInputStream(in);
            T[] result = (T[]) Array.newInstance(clz, numOfItems);
            for (int i = 0; i < numOfItems; ++i) {
                result[i] = (T) jois.readObject();
            }
            jois.close();
            return result;
        }
	}
}
