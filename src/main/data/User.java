package data;

/**
 * User
 * <p>
 * represents a user, encapsulates user info and provides methods to manage user details
 *
 * @author Chen Yang, section 24
 * @version 3/28/25
 */
public class User extends Table implements IUser {

    //this is user info

    @TableField(field = "username", index = 1)
    private String username;

    @TableField(field = "password", index = 2)
    private String password;

    @TableField(field = "balance", index = 3)
    private double balance;

    @TableField(field = "rating", index = 4)
    private double rating;

    // Required for Table
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        balance = 0.0;
        rating = 0.0;
    }

    //getters and setters
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
}
