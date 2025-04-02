import java.io.*;
import java.util.ArrayList;


/** 
 * BaseDatabase Class. A base class to read/write to databases using arrays.
 * 
 * @author Karma Luitel, lab L24
 * @version 3/27/25
*/
public class Database implements IDatabase {

    private String filename;
    private String[] headers;
    private static Object gate = new Object();


    /* Planned format (headers) for databases:
     * 
     * User:
     * username,password,balance,rating
     * 
     * Listing:
     * id,seller,name,value,image,sold
     * 
     * Message:
     * sender,reciever,message,timestamp
     */

    //Initialize a database with headers
    public Database(String filename, String[] headers) {
        this.filename = filename;
        this.headers = headers;
    }

    private int headerIndexOf(String value) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private String arrayToDataLine(String[] values) {
        String toWrite = "\"";
        for (int i = 0; i < values.length; i++) {
            String line = values[i];
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '"') {
                    toWrite += "\"\"";
                } else {
                    toWrite += line.charAt(j);
                }
            }
            if (i != values.length - 1) {
                toWrite += "\",\"";
            }
        }
        toWrite += '\"';
        return toWrite;
    }

    private String[] dataLineToArray(String line) {
        String element = "";
        String[] parsed = new String[headers.length];
        int parseIdx = 0;
        for (int i = 0; i < line.length()-1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i+1) == '"') {
                element += '"';
                i++;
            } else if (line.charAt(i) == '"' && line.charAt(i+1) == ',') {
                parsed[parseIdx] = element;
                element = "";
                parseIdx++;
                i++;
            } else if (line.charAt(i) != '"') {
                element += line.charAt(i);
            }
        }
        parsed[parseIdx] = element;
        return parsed;
    }

    //Writes (appends) an array of string values to file.
    public void write(String[] values) throws DatabaseNotFoundException {
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), true))) {
            synchronized (gate) {
                pwr.println(arrayToDataLine(values));
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
    }

    //Updates an line of a given value in the database.
    public void update(String header, String value, String uHeader, String uValue) throws DatabaseNotFoundException {
        String file = "";
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            int checkIndex = headerIndexOf(header);
            int updateIndex = headerIndexOf(uHeader);
            while (line != null) {
                String[] parsed = dataLineToArray(line);
                if (checkIndex != -1) {
                    if (!parsed[checkIndex].equals(value)) {
                        file += line + '\n';
                    } else {
                        parsed[updateIndex] = uValue;
                        file += arrayToDataLine(parsed) + '\n';
                    }
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), false))) {
            synchronized (gate) {
                pwr.print(file);
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
    }

    //Updates an line in the database.
    public void update(String header, String value, String[] newrow) throws DatabaseNotFoundException {
        String file = "";
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            int checkIndex = headerIndexOf(header);
            while (line != null) {
                String[] parsed = dataLineToArray(line);
                if (checkIndex != -1) {
                    if (!parsed[checkIndex].equals(value)) {
                        file += line + '\n';
                    } else {
                        file += arrayToDataLine(newrow) + '\n';
                    }
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), false))) {
            synchronized (gate) {
                pwr.print(file);
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
    }

    //Finds lines with a specific key-value pair and returns in array form. Returns an empty array if no string is found.
    public ArrayList<String[]> get(String header, String value) throws DatabaseNotFoundException {
        ArrayList<String[]> values = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            int checkIndex = headerIndexOf(header);
            while(line != null) {
                String[] parsed = dataLineToArray(line);
                if (checkIndex != -1) {
                    if (parsed[checkIndex].equals(value)) {
                        values.add(parsed);
                    }
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
        return values;
    }

    //Deletes all lines containing a given key value pair.
    public void delete(String header, String value) throws DatabaseNotFoundException {
        String file = "";
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            int checkIndex = headerIndexOf(header);
            while (line != null) {
                String[] parsed = dataLineToArray(line);
                if (checkIndex != -1) {
                    if (!parsed[checkIndex].equals(value)) {
                        file += line + '\n';
                    }
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), false))) {
            synchronized (gate) {
                pwr.print(file);
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

}