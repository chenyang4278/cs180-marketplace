package packet;

import java.util.List;

/**
 * IPacketHeader
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public interface IPacketHeader {
    String getName();
    List<String> getValues();
    void addValue(String value);
}
