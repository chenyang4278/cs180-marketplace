package server.packet;

import java.util.List;

public interface IPacketHeader {
    String getName();
    List<String> getValues();
}
