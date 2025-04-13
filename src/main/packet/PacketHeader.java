package packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PacketHeader
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class PacketHeader implements IPacketHeader, Serializable {
    private String name;
    private List<String> values;

    public PacketHeader(String name) {
        this.name = name;
        this.values = new ArrayList<>();
    }

    public PacketHeader(String name, String... vals) {
        this.name = name;
        this.values = new ArrayList<>(Arrays.asList(vals));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getValues() {
        return values;
    }

    public void addValue(String value) {
        values.add(value);
    }
}
