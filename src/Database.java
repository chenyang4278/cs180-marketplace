import java.util.ArrayList;

interface Database {
    void write(String[] values) throws DatabaseNotFoundException;
    ArrayList<String[]> get(String header, String value) throws DatabaseNotFoundException;
    void delete(String header, String value) throws DatabaseNotFoundException;
    String getFilename();
    void setFilename(String filename);
}
