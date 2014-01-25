package serializers.msgpack;

import org.msgpack.MessagePack;

public final class ManualSerializer<T> extends AbstractSerializer<T> {
 public ManualSerializer(TypeHandler<T> handler, MessagePack msgpack) {
     super("msgpack-manual", handler, msgpack);
     handler.registerManually(msgpack);
 }
}
