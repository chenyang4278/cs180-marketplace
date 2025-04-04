import java.util.ArrayList;

/** 
 * IDatabase Class. A base database interface.
 * 
 * @author Karma Luitel, lab L24
 * @version 3/27/25
*/
interface IDatabase {
    void write(String[] values) throws DatabaseNotFoundException;
    ArrayList<String[]> get(String header, String value) throws DatabaseNotFoundException;
    void update(String header, String value, String uHeader, String uValue) throws DatabaseNotFoundException;
    void update(String header, String value, String[] newrow) throws DatabaseNotFoundException;
    void delete(String header, String value) throws DatabaseNotFoundException;
    String getFilename();
    void setFilename(String filename);
}
