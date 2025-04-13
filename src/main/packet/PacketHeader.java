package packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String value : values) {
            builder.append(name).append(":").append(value).append("\n");
        }

        return builder.toString();
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
