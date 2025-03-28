import java.util.ArrayList;

/**
 * User
 * <p>
 * represents a user, encapsulates user info and provides methods to manage user details
 *
 * @author Chen Yang, section 24
 * @version 3/28/25
 */
public class User extends Serializable implements IUser {

    //this is user info
    @SerializableField(field = "id", index = 0)
    private int id;

    @SerializableField(field = "username", index = 1)
    private String username;

    @SerializableField(field = "password", index = 2)
    private String password;

    @SerializableField(field = "balance", index = 3)
    private double balance;

    @SerializableField(field = "rating", index = 4)
    private double rating;

    private ArrayList<String> listings;
    private ArrayList<String> inbox;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        balance = 0.0;
        rating = 0.0;
        //id = 0;
        listings = new ArrayList<>();
        inbox = new ArrayList<>();

    }

    //getters and settrs

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {

        return balance;
    }

    public void setBalance(double balance) {

        this.balance = balance;
    }

    public double getRating() {

        return rating;
    }

    public void setRating(double rating) {

        this.rating = rating;
    }

    public ArrayList<String> getListings() {

        return listings;
    }

    public void setListings(ArrayList<String> listings) {
        this.listings = listings;
    }

    public ArrayList<String> getInbox() {

        return inbox;
    }

    public void setInbox(ArrayList<String> inbox) {
        this.inbox = inbox;
    }


    public void addListing(String item) {
        listings.add(item);
    }

    public void removeListing(String item) {
        listings.remove(item);
    }

    public void sendMessage(String message) {
        inbox.add(message);
    }

    public void removeMessage(String message) {
        inbox.remove(message);
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void deleteAccount() {
        username = null;
        password = null;
        balance = 0.0;
        rating = 0.0;
        listings.clear();
        inbox.clear();
    }

    public void saveToDatabase() throws DatabaseWriteException {
        if (this.getId() == 0) {
            try {
                User existingUser = DatabaseWrapper.get().getById(User.class, this.getId());
                this.setId(existingUser.getId());
            } catch (RowNotFoundException e) {
                this.setId(1);
            }
        }
        DatabaseWrapper.get().save(this);
    }

    public static User getById(int userId) throws RowNotFoundException {
        return DatabaseWrapper.get().getById(User.class, userId);
    }

    public static ArrayList<User> getUsersByColumn(String column, String value) throws RowNotFoundException {
        return new ArrayList<>(DatabaseWrapper.get().filterByColumn(User.class, column, value));
    }

    public String[] asRow() {
        return new String[]{username, password, String.valueOf(balance), String.valueOf(rating)};
    }

}
