package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.msgpack.MessagePack;
import org.msgpack.MessagePacker;
import org.msgpack.MessageUnpacker;
import org.msgpack.Packer;
import org.msgpack.Unpacker;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.util.codegen.DynamicPacker;
import org.msgpack.util.codegen.DynamicUnpacker;

import serializers.JsonJacksonDatabind.GenericSerializer;
import data.media.MediaContent;

public class MsgPack
{
    public static void register(TestGroups groups)
    {
        GenericSerializer<MediaContent> ser = new GenericSerializer<MediaContent>(MediaContent.class);
        groups.media.add(JavaBuiltIn.MediaTransformer, ser);
    }

    public static class GenericSerializer<T> extends Serializer<T>
    {
        private final MessagePacker msgPacker;
        private final MessageUnpacker msgUnpacker;
        
        public GenericSerializer(Class<T> t)
        {
            msgPacker = DynamicPacker.create(t);
            msgUnpacker = DynamicUnpacker.create(t);
        }
        
        public String getName() { return "msgpack/dynamic"; }

        public byte[] serialize(T data) throws IOException
        {
            ByteArrayOutputStream out = outputStream(data);
            Packer packer = new Packer(out);
            msgPacker.pack(packer, data);
            return out.toByteArray();
        }

        @SuppressWarnings("unchecked")
        public T deserialize(byte[] array) throws Exception
        {
            Unpacker unpacker = new Unpacker(new ByteArrayInputStream(array));
            return (T) msgUnpacker.unpack(unpacker);
        }
    }
}
