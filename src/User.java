import java.util.ArrayList;

public class User implements IUser {

    //this is user info
    private String username;
    private String password;
    private double balance;
    private double rating;

    //for listing and messages idk how it's stored in database yet
    private ArrayList<String> listings;
    private ArrayList<String> inbox;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        balance = 0.0;
        rating = 0.0;
        listings = new ArrayList<>();
        inbox = new ArrayList<>();

    }
}
