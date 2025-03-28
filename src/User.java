import java.util.ArrayList;
import java.io.*;

public class User extends Serializable, implements Serializable, IUser {

    //this is user info
    private String username;
    private String password;
    private double balance;
    private double rating;
    private int id;

    //for listing and messages idk how it's stored in database yet
    private ArrayList<String> listings;
    private ArrayList<String> inbox;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        balance = 0.0;
        rating = 0.0;
        id = 0;
        listings = new ArrayList<>();
        inbox = new ArrayList<>();

    }

    //getters and settrs

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }

    public double getBalance() {

        return balance;
    }

    public double getRating() {

        return rating;
    }

    public ArrayList<String> getListings() {

        return listings;
    }

    public ArrayList<String> getInbox() {

        return inbox;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public void setBalance(double balance) {

        this.balance = balance;
    }

    public void setRating(double rating) {

        this.rating = rating;
    }

    //add item to listings
    public void addListing(String item) {
        listings.add(item);
    }

    //take out item from listing
    public void removeListing(String item) {
        listings.remove(item);
    }

    //add message to inbox
    public void sendMessage(String message) {
        inbox.add(message);
    }

    //delete/reset account but tbh idk if I should write it like this lol idk how implemented in database
    public void deleteAccount() {
        username = null;
        password = null;
        balance = 0.0;
        rating = 0.0;
        listings.clear();
        inbox.clear();
    }

    public void saveToDatabase() throws DatabaseWriteException{
        if (this.id == 0){
            this.id = DatabaseWrapper.get().getNextID(User.class);
        }
        DatabaseWrapper.get().save(this);
    }

    public static User getById(int userId) throws RowNotFoundException{
        return DatabaseWrapper.get().getById(User.class, userId);
    }

    public static ArrayList<User> getUsersByColumn(String column, String value){
        return DatabaseWrapper.get().filterByColumn(User.class, column, value);
    }


}
