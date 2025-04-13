package packet.response;

import database.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class ObjectListPacket<T extends Table> extends SuccessPacket implements Serializable {
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