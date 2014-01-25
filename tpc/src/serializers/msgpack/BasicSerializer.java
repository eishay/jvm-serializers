package serializers.msgpack;

import org.msgpack.MessagePack;

public final class BasicSerializer<T> extends AbstractSerializer<T> {
    public BasicSerializer(TypeHandler<T> handler, MessagePack msgpack) {
        super("msgpack-databind", handler, msgpack);
        handler.register(msgpack);
    }
}