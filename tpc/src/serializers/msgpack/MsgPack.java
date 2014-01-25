package serializers.msgpack;

import org.msgpack.MessagePack;

import serializers.JavaBuiltIn;
import serializers.TestGroup;
import serializers.TestGroups;
import serializers.Transformer;

public class MsgPack
{
    public static void register(TestGroups groups) {
        register(groups.media, JavaBuiltIn.mediaTransformer, new MediaContentTypeHandler());
    }

    private static <T,S> void register(TestGroup<T> group, Transformer<T,S> transformer, TypeHandler<S> handler) {
        group.add(transformer, new BasicSerializer<S>(handler, new MessagePack()));
        //group.add(transformer, new ManualSerializer<S>(handler, new MessagePack()));
    }
}