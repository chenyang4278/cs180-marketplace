import static org.junit.Assert.*;
import org.junit.Test;

/**
 * TestListing.java
 *
 * JUnit tests for the Listing class.
 *
 * @author Benny Huang
 * @version April 6, 25
 */

public class TestListing {

    @Test
    public void testDefaultConstructorAndSetters() {
        Listing listing = new Listing();
        
        listing.setListingId(69420);
        listing.setSellerId(89344);
        listing.setSellerName("Benjamin");
        listing.setTitle("Pokemon Cards");
        listing.setDescription("Near Mint Milotic SIR from Surging Sparks.");
        listing.setPrice(142.99);
        listing.setSold(false);
        
        assertEquals(69420, listing.getListingId());
        assertEquals(89344, listing.getSellerId());
        assertEquals("Benjamin", listing.getSellerName());
        assertEquals("Pokemon Cards", listing.getTitle());
        assertEquals("Near Mint Milotic SIR from Surging Sparks.", listing.getDescription());
        assertEquals(142.99, listing.getPrice(), 0.001);
        assertFalse(listing.isSold());
    }

    @Test
    public void testParameterizedConstructor() {
        Listing listing = new Listing(8910, "Benny", "Watch Band", 
                                      "Watch Band for a 42 millimeter watch.", 10.99, true);
      
        listing.setListingId(202);
      
        assertEquals(202, listing.getListingId());
        assertEquals(8910, listing.getSellerId());
        assertEquals("Benny", listing.getSellerName());
        assertEquals("Watch Band", listing.getTitle());
        assertEquals(
          "Watch Band for a 42 millimeter watch.", listing.getDescription());
        assertEquals(10.99, listing.getPrice(), 0.001);
        assertTrue(listing.isSold());
    }

    @Test
    public void testToString() {
        Listing listing = new Listing(888, "Egan", "Charizard", "Charizard is cool.", 500.00, false);
        listing.setListingId(777);
        
        String expected = "Listing: id = 777, sellerId = 888, sellerName = Egan, title = Charizard, description = Charizard is cool., price = 500.00, sold = false.";
        assertEquals(expected, listing.toString());
    }
}
