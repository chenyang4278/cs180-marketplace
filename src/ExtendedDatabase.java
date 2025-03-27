public interface ExtendedDatabase {
    public IUser createUser(String username, String password, int balance);
    public IUser getUser(int id);
    public IUser getUser(String username);
    public IUser[] searchUsers(String query);
    public Listing createListing(int sellerId, String title, String category, int price);
    public Listing getListing(int id);
    public void deleteListing(int id);
    public void markListingBought(int id, int buyerId);
    public Listing[] searchListings(String query);
    public Listing[] getSoldItems(int sellerId);
}
