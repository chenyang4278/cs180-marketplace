package packet.response;

import database.Table;

import java.io.Serializable;

public class ObjectPacket<T extends Table> extends SuccessPacket implements Serializable {
    private T obj;

    public ObjectPacket(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
