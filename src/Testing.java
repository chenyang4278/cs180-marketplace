public class Testing {
    
    public static void main(String[] args) {
        BaseDatabase user_db = new BaseDatabase("user_db.txt");

        user_db.write("username:karma,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5");
        user_db.write("username:karma2,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5");
        user_db.write("username:karma3,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5");
        user_db.write("username:karma4,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5");
        
        System.out.println(user_db.find("username", "karma3"));

        user_db.delete("username", "karma3");

        System.out.println(user_db.find("username", "karma3"));
        System.out.println(user_db.find("username", "karma"));

        user_db.write("username:karma5,password:12345,listings:[a,b,c,d,e],balance:192.21,rating:5");
        
        System.out.println(user_db.find("username", "karma5"));




    }
}
