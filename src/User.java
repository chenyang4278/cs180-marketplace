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

    @SerializableField(field = "username", index = 1)
    private String username;

    @SerializableField(field = "password", index = 2)
    private String password;

    @SerializableField(field = "balance", index = 3)
    private double balance;

    @SerializableField(field = "rating", index = 4)
    private double rating;

    private ArrayList<Listing> listings;
    private ArrayList<Message> inbox;

    // Required for Serializable
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        balance = 0.0;
        rating = 0.0;
        listings = new ArrayList<>();
        inbox = new ArrayList<>();

    }

    //getters and settrs
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

    public ArrayList<Listing> getListings() {
        if (listings == null) {
            try {
                listings = (ArrayList<Listing>) DatabaseWrapper.get().filterByColumn(Listing.class, "seller_id", String.valueOf(this.getId()));
            } catch (RowNotFoundException e) {
                listings = new ArrayList<>();
            }
        }
        return listings;
    }


    public ArrayList<Message> getInbox() {
        if (inbox == null) {
            try {
                inbox = (ArrayList<Message>) DatabaseWrapper.get().filterByColumn(Message.class, "receiver_id", String.valueOf(this.getId()));
            } catch (RowNotFoundException e) {
                inbox = new ArrayList<>();
            }
        }
        return inbox;
    }

    public void setInbox(ArrayList<Message> inbox) {
        this.inbox = inbox;
    }


    //waiting on listing class
    public void createListing(Listing item) {
        try{
            DatabaseWrapper.get().save(item);
            listings.add(item);
        } catch(DatabaseWriteException e){
            System.out.println("Error addingn listing: " + e.getMessage());
        }

    }

    public void removeListing(Listing item) {
        listings.remove(item);
    }

    public void sendMessage(String messageContent, int receiverId) {
        try{
            Message message = new Message(this.getId(), receiverId, messageContent);
            message.save();
            inbox.add(message);
        } catch(DatabaseWriteException e){
            System.out.println("Error sending message: " + e.getMessage());
        }

    }

    public void removeMessage(Message message) {
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



    public static User getById(int userId) throws RowNotFoundException {
        return DatabaseWrapper.get().getById(User.class, userId);
    }

    public static ArrayList<User> getUsersByColumn(String column, String value) throws RowNotFoundException {
        return new ArrayList<>(DatabaseWrapper.get().filterByColumn(User.class, column, value));
    }


}
