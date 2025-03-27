import java.util.ArrayList;

interface Database {
    void write(ArrayList<String> values) throws DatabaseNotFoundException;
    ArrayList<ArrayList<String>> get(String header, String value) throws DatabaseNotFoundException;
    void delete(String header, String value) throws DatabaseNotFoundException;
    String getFilename();
    void setFilename(String filename);
}
