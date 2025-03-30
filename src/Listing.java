/**
 *
 * Listing.java
 * 
 * @author Benny Huang, lab sec 024
 *
 * @version March 30th, 2025
 *
 */

public class Listing extends Serializable implements IListing {

  @SerializableField(field = "seller_id", index = 1)
  private int sellerId;
  
  @SerializableField(field = "seller_name", index = 2)
  private String sellerName;
  
  @SerializableField(field = "title", index = 3)
  private String title;
  
  @SerializableField(field = "description", index = 4)
  private String description;

  @SerializableField(field = "price", index = 5)
  private double price;
  
  @SerializableField(field = "image", index = 6)
  private String image;
  
  @SerializableField(field = "sold", index = 7)
  private boolean sold;
    
  public Listing() {}
    
  public Listing(int sellerId, String sellerName, String title, String description, 
                 double price, String image, boolean sold) {
    this.sellerId = sellerId;
    this.sellerName = sellerName;
    this.title = title;
    this.description = description;
    this.price = price;
    this.image = image;
    this.sold = sold;
  }

  @Override
  public int getListingId() {
    return getId();
  }

  @Override
  public void setListingId(int id) {
    setId(id);
  }

  @Override
  public int getSellerId() {
    return sellerId;
  }

  @Override
  public void setSellerId(int sellerId) {
    this.sellerId = sellerId;
  }

  @Override
  public String getSellerName() {
    return sellerName;
  }

  @Override
  public void setSellerName(String sellerName) {
    this.sellerName = sellerName;
  }
  
  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public void setPrice(double price) {
    this.price = price;
  }

  @Override
  public String getImage() {
    return image;
  }

  @Override
  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public boolean isSold() {
    return sold;
  }

  @Override
  public void setSold(boolean sold) {
    this.sold = sold;
  }
    
  public String toString() {
    return "Listing [id=" + getId() + ", sellerId=" + sellerId + ", sellerName=" + sellerName
            + ", title=" + title + ", description=" + description + ", price=" + price 
            + ", image=" + image + ", sold=" + sold + "]";
  }
}
