package data;

/**
 *
 * IListing.java
 * <p>
 * This interface creates a
 * list of methods that are
 * used by the listing class.
 * 
 * @author Benny Huang, sec 024
 * @version April 06, 2025
 *
 */

public interface IListing {
    int getListingId();

    void setListingId(int id);

    int getSellerId();

    void setSellerId(int sellerId);

    String getSellerName();

    void setSellerName(String sellerName);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    double getPrice();

    void setPrice(double price);

    boolean isSold();

    void setSold(boolean sold);

    String getImage();

    void setImage(String image);

    String toString();
}
