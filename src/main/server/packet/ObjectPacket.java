package server.packet;

import database.Serializable;

import java.util.ArrayList;

public class ObjectPacket<T extends Serializable> extends SuccessPacket {
    public ObjectPacket(T obj) {
        // need method to convert object to csv format
        super(new ArrayList<PacketHeader>(), "convert obj to its row format here");
    }
}
