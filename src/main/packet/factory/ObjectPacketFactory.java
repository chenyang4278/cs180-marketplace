package packet.factory;

import database.DatabaseWrapper;
import database.Serializable;
import packet.Packet;

public class ObjectPacketFactory<T extends Serializable> extends SuccessPacketFactory {
    private T obj;

    private final DatabaseWrapper db;

    public ObjectPacketFactory(T obj) {
        this.obj = obj;

        db = DatabaseWrapper.get();
    }

    @Override
    public Packet create() {
        Packet packet = super.create();

        packet.setBody(db.objAsString(obj));

        return packet;
    }

    public static <T extends Serializable> ObjectPacketFactory<T> parse(Class<T> cls, Packet packet) {
        return new ObjectPacketFactory<T>(DatabaseWrapper.get().stringAsObj(cls, packet.getBody()));
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
