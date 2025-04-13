package packet.response;

import database.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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