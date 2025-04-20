package packet;

import data.User;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * IPacket
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public interface IPacket {
    byte[] getBody();

    boolean getBodyContinues();

    String getPath();

    List<PacketHeader> getHeaders();

    void setPath(String path);

    void setHeaders(List<PacketHeader> headers);

    void setBody(byte[] body);

    void setBodyContinues(boolean bodyContinues);

    String[] getHeaderValues(String... keys);

    void addHeader(String name, String value);

    PacketHeader getHeader(String name);

    void write(OutputStream stream) throws IOException;
}
