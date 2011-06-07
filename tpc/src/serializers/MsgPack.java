package serializers;

import java.io.IOException;

import org.msgpack.*;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class MsgPack
{
    public static void register(TestGroups groups)
    {
        GenericSerializer<MediaContent> ser = new GenericSerializer<MediaContent>(MediaContent.class);
        groups.media.add(JavaBuiltIn.MediaTransformer, ser);
        /* Must either register or use @MessagePackMessage annotation; this is
         * bit less intrusive.
         * But we do need to add some @Optional's, otherwise msgpack craps on nulls,
         * with very poor error message, hard to debug...
         */
        MessagePack.register(MediaContent.class);
        MessagePack.register(Media.class);
        MessagePack.register(Media.Player.class);
        MessagePack.register(Image.class);
        MessagePack.register(Image.Size.class);
    }

    public static class GenericSerializer<T> extends Serializer<T>
    {
        protected final Class<T> type;
        
        public GenericSerializer(Class<T> t) {
            type = t;
        }
        
        public String getName() { return "msgpack/dynamic"; }

        public byte[] serialize(T data) throws IOException
        {
            return MessagePack.pack(data);
        }

        public T deserialize(byte[] array) throws Exception
        {
            return (T) MessagePack.unpack(array, type);
        }
    }
}
