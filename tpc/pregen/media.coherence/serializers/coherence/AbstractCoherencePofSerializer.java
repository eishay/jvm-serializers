package serializers.coherence;

import com.tangosol.io.ByteArrayReadBuffer;
import com.tangosol.io.ByteArrayWriteBuffer;
import com.tangosol.io.ReadBuffer;
import com.tangosol.io.WriteBuffer;
import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.util.Binary;
import serializers.Serializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

public abstract class AbstractCoherencePofSerializer<T> extends Serializer<T> {

        public static int BUFFERSIZE_BYTES = 512;

        private final Class<T> clz;
        private final ConfigurablePofContext tx;

        public AbstractCoherencePofSerializer(Class<T> c, String pofConfig) {
            clz = c;
            tx = new ConfigurablePofContext(pofConfig);
        }

         public abstract String getName();

        @SuppressWarnings("unchecked")
        public T deserialize(byte[] array) throws Exception {
            ReadBuffer readBuffer = new ByteArrayReadBuffer(array);
            return (T) tx.deserialize(readBuffer.getBufferInput());

        }

        public byte[] serialize(T data) throws IOException {
            WriteBuffer writeBuffer = new ByteArrayWriteBuffer(BUFFERSIZE_BYTES);
            tx.serialize(writeBuffer.getBufferOutput(), data);
            return writeBuffer.toByteArray();
        }

        @Override
        public final void serializeItems(T[] items, OutputStream out) throws Exception {
            WriteBuffer writeBuffer = new ByteArrayWriteBuffer(BUFFERSIZE_BYTES*items.length);
            for (Object item : items) {
                tx.serialize(writeBuffer.getBufferOutput(), (T) item);
            }
            out.write(writeBuffer.toByteArray());

        }

        @SuppressWarnings("unchecked")
        @Override
        public T[] deserializeItems(InputStream in, int numberOfItems) throws Exception {
            DataInputStream dio = new DataInputStream(in);
            ReadBuffer buffer = new Binary(dio);
            T[] result = (T[]) Array.newInstance(clz, numberOfItems);
            for (int i = 0; i < numberOfItems; ++i) {
                result[i] = (T) tx.deserialize(buffer.getBufferInput());
            }

            return result;
        }
}

