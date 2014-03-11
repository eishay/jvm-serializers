package serializers;

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

    public static void register (TestGroups groups) {
        register(groups.media, JavaBuiltIn.mediaTransformer);
    }

    private static <T, S> void register (TestGroup<T> group, Transformer<T, S> transformer) {
        group.add(transformer, new BasicSerializer<S>("fst-flat-pre",true,true),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.CLASSES_KNOWN, 
                        "fst in unshared mode with preregistered classes"
                )
        );

        group.add(transformer, new BasicSerializer<S>("fst-flat",true,false),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FLAT_TREE,
                        SerClass.ZERO_KNOWLEDGE,
                        "fst default, but unshared mode"
                )
        );
        group.add(transformer, new BasicSerializer<S>("fst",false,false),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.ZERO_KNOWLEDGE,
                        "default: JDK serialization drop-in-replacement mode"
                )
        );

    }

    // ------------------------------------------------------------
    // Serializers

    /**
     * setup similar to kryo
     */
    public static class BasicSerializer<T> extends Serializer<T> {
        final static FSTConfiguration confUnsharedUnregistered;
        final static FSTConfiguration confUnsharedRegister;
        final static FSTConfiguration confShared;
        static {
//            System.setProperty("fst.unsafe", "true");
            confUnsharedUnregistered = FSTConfiguration.createDefaultConfiguration();
            confUnsharedUnregistered.setShareReferences(false);

            confUnsharedRegister = FSTConfiguration.createDefaultConfiguration();
            confUnsharedRegister.setShareReferences(false);
            confUnsharedRegister.registerClass(
                    Image.Size.class,
                    Image.class,
                    Media.Player.class,
                    Media.class,
                    MediaContent[].class,
                    MediaContent.class,
                    MediaContent.class);
            
            confShared = FSTConfiguration.createDefaultConfiguration();
        }

        FSTObjectInput objectInput;
        FSTObjectOutput objectOutput;
        boolean unshared;
        String name;
        Class type[] = { MediaContent.class };

        public BasicSerializer (String name, boolean flat, boolean register) {
            this.name = name;
            this.unshared = flat;
            if ( flat ) {
                if ( register ) {
                    objectInput = new FSTObjectInputNoShared(confUnsharedRegister);
                    objectOutput = new FSTObjectOutputNoShared(confUnsharedRegister);
                } else {
                    objectInput = new FSTObjectInputNoShared(confUnsharedUnregistered);
                    objectOutput = new FSTObjectOutputNoShared(confUnsharedUnregistered);
                }
            } else {
                objectInput = new FSTObjectInput(confShared);
                objectOutput = new FSTObjectOutput(confShared);
            }
        }

        @SuppressWarnings("unchecked")
        public T deserialize (byte[] array) {
            return (T) deserializeInternal(array);
        }

        private Object deserializeInternal(byte[] array) {
            try {
                objectInput.resetForReuseUseArray(array);
                return objectInput.readObject(type);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        public byte[] serialize (T content) {
            try {
                objectOutput.resetForReUse();
                objectOutput.writeObject(content,type);
                return objectOutput.getCopyOfWrittenBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void serializeItems (T[] items, OutputStream outStream) throws Exception {
            objectOutput.resetForReUse();
            for (int i = 0; i < items.length; i++) {
                objectOutput.writeObject(items[i]);
            }
            outStream.write(objectOutput.getBuffer(),0,objectOutput.getWritten()); // avoid copy
        }

        @SuppressWarnings("unchecked")
        public T[] deserializeItems (InputStream inStream, int numberOfItems) throws IOException {
            try {
                MediaContent[] result = new MediaContent[numberOfItems];
                objectInput.resetForReuse(inStream);
                for ( int i=0; i < numberOfItems; i++)
                    result[i] = (MediaContent) objectInput.readObject();
                return (T[]) result;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getName () {
            return name;
        }
    }

}
