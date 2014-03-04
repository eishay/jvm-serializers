package serializers;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import data.media.*;
import de.ruedigermoeller.serialization.*;

import java.io.*;

/**
 * Copyright (c) 2012, Ruediger Moeller. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p/>
 * Date: 01.03.14
 * Time: 23:59
 * To change this template use File | Settings | File Templates.
 */
public class FastSerialization {

    public static final int BUFFER_SIZE = Math.max(
            Integer.getInteger("buffer_size", 1024), 256);

    public static void register (TestGroups groups) {
        register(groups.media, JavaBuiltIn.mediaTransformer);
    }

    private static <T, S> void register (TestGroup<T> group, Transformer<T, S> transformer) {
        group.add(transformer, new BasicSerializer<S>());
    }

    // ------------------------------------------------------------
    // Serializers

    /**
     * all default. Even omit registration, do not preallocate streams as others do ..
     */
    public static class BasicSerializer<T> extends Serializer<T> {
        final static FSTConfiguration conf;
        static {
//            System.setProperty("fst.unsafe", "true");
            conf = FSTConfiguration.createDefaultConfiguration();
            conf.setShareReferences(false);
            conf.registerClass(Image.Size.class, Image.class, Media.Player.class, Media.class, MediaContent[].class, MediaContent.class, MediaContent.class);
//            conf.setPreferSpeed(true);
        }
        private final byte[] buffer = new byte[BUFFER_SIZE];
        FSTObjectInput objectInput = new FSTObjectInputNoShared(conf);
        FSTObjectOutput objectOutput = new FSTObjectOutputNoShared(conf);


        public BasicSerializer () {
            objectOutput.resetForReUse(buffer);
        }

        public T deserialize (byte[] array) {
            return (T) deserializeInternal(array);
        }

        private Object deserializeInternal(byte[] array) {
            try {
                objectInput.resetForReuseUseArray(array);
                return objectInput.readObject();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        public byte[] serialize (T content) {
            return serializeInternal(content);
        }

        private byte[] serializeInternal(Object content) {
            try {
                objectOutput.resetForReUse();
                objectOutput.writeObject(content);
                return objectOutput.getCopyOfWrittenBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void serializeItems (T[] items, OutputStream outStream) throws Exception {
            outStream.write(serializeInternal(items));
        }

        @SuppressWarnings("unchecked")
        public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
            try {
                objectInput.resetForReuse(inStream);
                T[] res = (T[]) objectInput.readObject();
                objectInput.close();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getName () {
            return "fast-serialization";
        }
    }

}
