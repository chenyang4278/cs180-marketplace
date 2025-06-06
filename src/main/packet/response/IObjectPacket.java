package packet.response;

import data.Table;

/**
 * IObjectListPacket
 *
 * @param <T>
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IObjectPacket<T extends Table> {
    T getObj();

    void setObj(T obj);
}
