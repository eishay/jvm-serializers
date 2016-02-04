package serializers.msgpack;

import org.msgpack.MessagePack;

abstract class TypeHandler<T> {
    public final Class<T> type;

    protected TypeHandler(Class<T> type) {
        this.type = type;
    }

    public abstract void register(final MessagePack msgpack);

    public abstract void registerManually(final MessagePack msgpack);
}