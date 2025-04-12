package packet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface IPacket {
    String getPath();
    List<PacketHeader> getHeaders();
    void setPath(String path);
    void setHeaders(List<PacketHeader> headers);

    void addHeader(String name, String value);
    PacketHeader getHeader(String name);
    void write(OutputStream stream) throws IOException;
}
