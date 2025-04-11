package server.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketHeader implements IPacketHeader {
    private String name;
    private List<String> values;

    public PacketHeader(String name, String value) {
        this.name = name;
        this.values = new ArrayList<>(1);
        this.values.add(value);
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
