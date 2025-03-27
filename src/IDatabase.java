import java.util.ArrayList;

interface IDatabase {
    void write(String[] values) throws DatabaseNotFoundException;
    ArrayList<String[]> get(String header, String value) throws DatabaseNotFoundException;
    void update(String header, String value, String uHeader, String uValue) throws DatabaseNotFoundException;
    void delete(String header, String value) throws DatabaseNotFoundException;
    String getFilename();
    void setFilename(String filename);
}
