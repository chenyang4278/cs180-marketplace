package packet.response;

import data.Table;

/**
 * IObjectListPacket
 *
 * @author Karma Luitel
 * @version 4/14/25
 * @param <T>
 */
public interface IObjectPacket<T extends Table> {
    T getObj();
    void setObj(T obj);
}
