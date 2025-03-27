import java.io.*;

public class BaseDatabase implements Database {

    private String filename;

    public BaseDatabase(String filename) {
        this.filename = filename;
    }

    private boolean stringContains(String main, String check) {
        if (main.indexOf(check) != -1) {
            return true;
        }
        return false;
    }

    //Written text cannot be empty or contain \n or [] characters.
    //Example format for users:
    //username:karma,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5
    //Example format for listings:
    //id:id1,seller:karma,name:apple,price:1,image:none,sold:true

    //Writes (appends) a line to file. Returns true if sucessful. Make sure it is formatted beforhand.
    public boolean write(String text) {
        try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), true))) {
            pwr.println(text);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //Returns an empty line if no string is found.
    public String find(String key, String value) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            String check = key + ":" + value;
            while(line != null) {
                if (stringContains(line, check)) {
                    return line;
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            return "";
        }
        return "";
    }

    //Deletes a line containing a given key value pair. Returns true if sucessful.
    public boolean delete(String key, String value) {
        String file = "";
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)))) {
            String line = bfr.readLine();
            String check = key + ":" + value;
            while (line != null) {
                if (!stringContains(line, check)) {
                    file += line + '\n';
                }
                line = bfr.readLine();
            }
        } catch (IOException e) {
            return false;
        }
        if (file.length() > 0) {
            try (PrintWriter pwr = new PrintWriter(new FileOutputStream(new File(filename), false))) {
                pwr.print(file);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}