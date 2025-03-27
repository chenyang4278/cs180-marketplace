import java.io.*;
import java.util.ArrayList;


/** 
 * BaseDatabase Class. A base class to read/write to databases using arrays.
 * 
 * @author Karma Luitel, lab L24
 * @version 3/27/25
*/
public class BaseDatabase implements Database {

    private String filename;
    private ArrayList<String> headers;


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
    public BaseDatabase(String filename, ArrayList<String> headers) {
        this.filename = filename;
        this.headers = headers;
    }

    private ArrayList<String> parseLineToArray(String line) {
        String element = "";
        ArrayList<String> parsed = new ArrayList<>();
        for (int i = 0; i < line.length()-1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i+1) == '"') {
                element += '"';
                i++;
            } else if (line.charAt(i) == '"' && line.charAt(i+1) == ',') {
                parsed.add(element);
                element = "";
                i++;
            } else if (line.charAt(i) != '"') {
                element += line.charAt(i);
            }
        }
        parsed.add(element);
        return parsed;
    }

    //Writes (appends) an array of string values to file.
    public void write(ArrayList<String> values) throws DatabaseNotFoundException {
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), true))) {
            String toWrite = "\"";
            for (int i = 0; i < values.size(); i++) {
                String line = values.get(i);
                for (int j = 0; j < line.length(); j++) {
                    if (line.charAt(j) == '"') {
                        toWrite += "\"\"";
                    } else {
                        toWrite += line.charAt(j);
                    }
                }
                if (i != values.size() - 1) {
                    toWrite += "\",\"";
                }
            }
            toWrite += '\"';
            pwr.println(toWrite);
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
    }

    //Finds lines with a specific key-value pair and returns in array form. Returns an empty array if no string is found.
    public ArrayList<ArrayList<String>> get(String header, String value) throws DatabaseNotFoundException {
        ArrayList<ArrayList<String>> values = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            while(line != null) {
                ArrayList<String> parsed = parseLineToArray(line);
                int checkIndex = headers.indexOf(header);
                if (checkIndex != -1) {
                    if (parsed.get(checkIndex).equals(value)) {
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
            while (line != null) {
                ArrayList<String> parsed = parseLineToArray(line);
                int checkIndex = headers.indexOf(header);
                if (checkIndex != -1) {
                    if (!parsed.get(checkIndex).equals(value)) {
                        file += line + '\n';
                    }
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new DatabaseNotFoundException("Invalid database");
        }
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), false))) {
            pwr.print(file);
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

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

}