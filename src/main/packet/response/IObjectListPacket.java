package packet.response;

import data.Table;

import java.util.ArrayList;

/**
 * IObjectListPacket
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IObjectListPacket<T extends Table> {
    ArrayList<T> getObjList();
    void setObjList(ArrayList<T> objList);
}
