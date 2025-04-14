package packet.response;

import data.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ObjectListPacket
 * <p>
 * Extends SuccessPacket with the ability to hold a list of table objects.
 *
 * @author Karma Luitel
 * @version 4/13/25
 *
 * @param <T> table class
 */
public class ObjectListPacket<T extends Table> extends SuccessPacket implements Serializable, IObjectListPacket<T> {
    private ArrayList<T> objList;

    public ObjectListPacket(ArrayList<T> objList) {
        this.objList = objList;
    }

    public ArrayList<T> getObjList() {
        return objList;
    }

    public void setObjList(ArrayList<T> objList) {
        this.objList = objList;
    }
}