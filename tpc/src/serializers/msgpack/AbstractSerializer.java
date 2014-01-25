package serializers.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;

import serializers.Serializer;

public abstract class AbstractSerializer<T> extends Serializer<T> {
    protected final String name;
    protected final Class<T> type;
    private final BufferPacker packer;
    private final BufferUnpacker unpacker;
    
    protected AbstractSerializer(String name, TypeHandler<T> handler, MessagePack msgpack) {
        this.name = name;
        type = handler.type;
        packer = msgpack.createBufferPacker(BUFFER_SIZE);
        unpacker = msgpack.createBufferUnpacker();
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