package serializers.msgpack;

import org.msgpack.MessagePack;

import serializers.*;

public class MsgPack
{
    private final static String DESC = 
            "uses positional (column) layout (instead of Maps std impl uses) to eliminate use of names";
    
    public static void register(TestGroups groups) {
        register(groups.media, JavaBuiltIn.mediaTransformer);
    }

    @SuppressWarnings("unchecked")
    private static <T,S> void register(TestGroup<T> group, Transformer<T,S> transformer) {
        MessagePack msgpack = new MessagePack();
        TypeHandler<S> h = (TypeHandler<S>) new MediaContentTypeHandler();
        h.register(msgpack);
        group.add(transformer, new MsgPackSerializer<S>("msgpack/databind", h, msgpack),
                new SerFeatures(SerFormat.BIN_CROSSLANG, SerGraph.FLAT_TREE, SerClass.CLASSES_KNOWN, DESC));

        msgpack = new MessagePack();
        h = (TypeHandler<S>) new MediaContentTypeHandler();
        h.registerManually(msgpack);

        group.add(transformer, new MsgPackSerializer<S>("msgpack/manual", h, msgpack),
                new SerFeatures( SerFormat.BIN_CROSSLANG,SerGraph.FLAT_TREE,SerClass.MANUAL_OPT, DESC));
    }
}