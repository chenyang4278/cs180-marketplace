public interface ExtendedDatabase {
    public User createUser(String username, String password, int balance);
    public User getUser(int id);
    public User getUser(String username);
    public User[] searchUsers(String query);
    public Listing createListing(int sellerId, String title, String category, int price);
    public Listing getListing(int id);
    public void deleteListing(int id);
    public void markListingBought(int id, int buyerId);
    public Listing[] searchListings(String query);
    public Listing[] getSoldItems(int sellerId);
}
