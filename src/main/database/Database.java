package database;

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
    private static final Object GATE = new Object();

    /* Planned format (headers) for databases:
     * User:
     * username,password,balance,rating
     *
     * Listing:
     * id,seller,name,value,sold
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
                if (line.charAt(j) == '\n') {
                    toWrite += "\\n";
                } else if (line.charAt(j) == '\\') {
                    toWrite += "\\\\";
                } else if (line.charAt(j) == '"') {
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
        String[] row = new String[headers.length];

        int rowI = 0;
        boolean inQuotes = false;
        int quoteStart = 0;
        for (int i = 0; i < line.length(); i++) {
            char chr = line.charAt(i);

            if (!inQuotes) {
                if (chr == '\"') {
                    inQuotes = true;
                    quoteStart = i + 1;
                } else if (chr == ',') {
                    row[rowI] = readBackslashes(line.substring(quoteStart, i - 1))
                            .replaceAll("\"\"", "\"");
                    rowI++;
                }
                continue;
            }

            if (chr == '\"') {
                if (i + 1 == line.length() || line.charAt(i + 1) != '\"') {
                    inQuotes = false;
                } else {
                    i++;
                }
            }
        }
        row[rowI] =  readBackslashes(line.substring(quoteStart, line.length() - 1))
                .replaceAll("\"\"", "\"");
        return row;
    }

    private String readBackslashes(String s) {
        String ans = "";
        for (int i = 0; i < s.length(); i++) {
            if (i+2 < s.length() && s.substring(i,i+2).equals("\\\\")) {
                ans += "\\";
                i++;
            } else if (i+2 < s.length() && s.substring(i,i+2).equals("\\n")) {
                ans += "\n";
                i++;
            } else {
                ans += s.charAt(i);
            }
        }
        return ans;
    }

    //Writes (appends) an array of string values to file.
    public void write(String[] values) throws DatabaseNotFoundException {
        synchronized (GATE) {
            try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), true))) {
                pwr.println(arrayToDataLine(values));
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
        }
    }

    //Updates an line of a given value in the database.
    public void update(String header, String value, String uHeader, String uValue) throws DatabaseNotFoundException {
        String file = "";
        synchronized (GATE) {
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
                pwr.print(file);
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
        }
    }

    //Updates an line in the database.
    public void update(String header, String value, String[] newrow) throws DatabaseNotFoundException {
        String file = "";
        synchronized (GATE) {
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
                pwr.print(file);
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
        }
    }

    //Finds lines with a specific key value pair and returns in array form.
    //Returns an empty array if no string is found.

    //Lenient will control how strict the comparison is, set to true for more lenience, false for exact
    public ArrayList<String[]> get(String header, String value, boolean lenient) throws DatabaseNotFoundException {
        ArrayList<String[]> values = new ArrayList<>();
        synchronized (GATE) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
                String line = bfr.readLine();
                int checkIndex = headerIndexOf(header);
                while (line != null) {
                    String[] parsed = dataLineToArray(line);
                    if (checkIndex != -1) {
                        if ((lenient && lenientStringCompare(parsed[checkIndex], value)) ||
                                (parsed[checkIndex].equals(value))) {
                            values.add(parsed);
                        }
                    }
                    line = bfr.readLine();
                }
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
        }
        return values;
    }

    //gets every row in the database
    public ArrayList<String[]> getAll() throws DatabaseNotFoundException {
        ArrayList<String[]> values = new ArrayList<>();
        synchronized (GATE) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
                String line = bfr.readLine();
                while (line != null) {
                    values.add(dataLineToArray(line));
                    line = bfr.readLine();
                }
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
        }
        return values;
    }

    //Edit if we need more or less leniency, currently does following:
    //remove spaces
    //remove capitalization
    private boolean lenientStringCompare(String s1, String s2) {
        String lS1 = "";
        String lS2 = "";
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) > 32) {
                lS1 += s1.toLowerCase().charAt(i);
            }
        }
        for (int i = 0; i < s2.length(); i++) {
            if (s2.charAt(i) > 32) {
                lS2 += s2.toLowerCase().charAt(i);
            }
        }
        return lS1.equals(lS2);
    }

    //Deletes all lines containing a given key value pair.
    public void delete(String header, String value) throws DatabaseNotFoundException {
        String file = "";
        synchronized (GATE) {
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
                pwr.print(file);
            } catch (IOException e) {
                throw new DatabaseNotFoundException("Invalid database");
            }
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