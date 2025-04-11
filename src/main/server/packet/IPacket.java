package server.packet;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface IPacket {
    String getPath();
    List<PacketHeader> getHeaders();
    String getBody();
    void setPath(String path);
    void setHeaders(List<PacketHeader> headers);
    void setBody(String body);

    void addHeader(String name, String value);
    void write(Writer writer) throws IOException;
}
