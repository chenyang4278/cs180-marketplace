package server.handlers;

import database.Listing;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ObjectListPacket;

import java.util.ArrayList;

public class GetListingsFromAttributeHandler extends PacketHandler {
    public GetListingsFromAttributeHandler() { super("/listingsattribute/"); }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     * attribute - arg in index 0
     * attributeval  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String attribute = packet.getHeader("attribute").getValues().get(0);
        String attributeVal = packet.getHeader("attributeval").getValues().get(0);
        ArrayList<Listing> listings = (ArrayList<Listing>) db.filterByColumn(Listing.class, attribute, attributeVal);
        return new ObjectListPacket<Listing>(listings);
    }
}