package packet.response;

import data.Table;

import java.io.Serializable;

/**
 * ObjectPacket
 * <p>
 * Extends SuccessPacket with the ability to hold a table object.
 *
 * @param <T> table class
 * @author Ayden Cline
 * @version 4/12/25
 */
public class ObjectPacket<T extends Table> extends SuccessPacket implements Serializable, IObjectPacket<T> {
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
