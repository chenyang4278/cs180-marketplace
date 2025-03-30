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

  String getImage(); //assuming that we want to import images through url
  voidd setImage(String image);

  boolean isSold();
  void setSold(boolean sold);
}
