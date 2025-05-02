# CS180 Team Project
- L24 Team 1: Karma Luitel, Ian Ogden, Ayden Cline, Chen Yang



## Compilation
### Phase 1
For now our project is a  set of Database and User/Listing/Message object classes, providing functionality for reading, writing, updating, and deleting object classes from a csv file database. Just compile every class and run test cases to see how the classes function.
### Phase 2
The `client` package needs to be compiled alongside the `packet` and `data` packages. The `server` package needs to compiled alongside the `database`, `packet`, and `data` packages. Run test cases to see how the classes function. For testing IO functionality, refer to `Server.java`, `ClientHandler.java`, and `Client.java`. These classes are our main Network IO classes. The project can optionally be compiled and tested using Maven.
### Phase 3
The `client` package needs to be compiled alongside the `packet` and `data` packages. The `server` package needs to compiled alongside the `database`, `packet`, and `data` packages. Test our project by first running `Server.java` in package `server`, then running `Program.java` in package `client`.



## Submissions
### Phase 1
Vocareum submission: Ayden Cline
### Phase 2
Vocareum submission: Karma Luitel
### Phase 3
Vocareum submission:
Report Brightspace submission:
Presentation Brightspace submission:



## Client
### Client.java
- Chen Yang
- Client is a class that handles communication between the frontend and the backend. It opens a socket connection to the server and sends/receives serialized packets. It does not store any local user or listing data; all data is retrieved through network calls to the server. Client only stores the user’s ID and session token after logging in. When user information is needed, the class calls the `/user/:id` endpoint to retrieve the most up-to-date user object. This ensures the user data is always fresh and consistent with the database. Client implements the IClient interface. IClient defines all required methods like login, user creation, listing creation, search, messaging, and data deletion. By implementing this interface, Client ensures consistency and makes testing or mocking easier. All packets are sent through three generic methods: sendObjectPacketRequest, sendObjectListPacketRequest, and sendSuccessPacketRequest. These methods construct a Packet, attach the session token, send the packet over the socket, and parse the appropriate type of response from the server. Each action such as creating a user, buying a listing, sending a message, has a specific method that builds the required headers, sends the request to the corresponding route, and returns either an object, a list, or a success result. Message retrieval calls the server twice — once for messages sent and once for messages received, then combines the results to return a complete conversation. The Client class also includes a static method called createHeaders. This utility is used to build a list of PacketHeader objects from alternating key-value Strings, to reduce repeated boilerplate when preparing requests. Overall, this class provides a clean and consistent API for interacting with the server. It is ready to be integrated with a frontend and supports full account, listing, and message functionality.
### ClientGUI.java
- Ayden Cline
- This class creates the base GUI frame and panels and implements functionality for switching between screens.
### Header.java
- Ayden Cline
- This class creates the header layout that appears on every screen. It contains buttons that can be clicked to switch between different screens.
### Styles.java
- Ayden Cline
- This is a small class with some static font fields to use consistent fonts across different parts of the GUI.
### Program.java
- Ayden Cline
- Serves as the starting point of the client program, instantiating `Client` and `ClientGUI`



## GUI Screens
### Screen.java
- Ayden Cline
- An abstract class to be extended by any screen class. It provides a reference class to be used by `ClientGUI` in referring to screen objects and implements two small utility functions for screen classes to use.
### AccountScreen.java
- Ayden Cline
- A screen that shows a user's information and two buttons for logging out and deleting their account.
### CreateListingScreen.java
- Karma Luitel
- Screen used to create a listing. GUI includes input field elements used for inputting values needed in a listing, as well as JFileChooser used to select images for listings. Works with several methods in `Client.java` to talk to the server to create listings.
### HomeScreen.java
- Ayden Cline
- A introduction to the program, showing its title and a short description of the program.
### ListingsScreen.java
- Chen Yang
### ViewListingPopup.java
- Chen Yang
### LoginScreen.java
- Karma Luitel
- Screen used to create an account or login. GUI includes input field elements for username and password, and has ability to switch between login or create account modes. Works with several methods in `Client.java` to talk to the server to create accounts or login an user.
### MessageScreen.java
- Ian Ogden
### MessageBubble.java
- Ian Ogden




## Data
### Table.java
- Ayden Cline
- This is an abstract class designed to serialize an object to a list of strings and deserialize a list of strings back into an object. The class is not meant to be used directly, hence why it’s an abstract class. The class also contains an annotated id attribute, getters, and setters to be inherited by database objects.
- Any class that extends `Table` defines its own fields annotated by the `TableField` annotation and provides an empty constructor. The classes that extend this one are `User`, `Message`, and `Listing` (aside from tests).
- When serializing an object (`Table.asRow`), the `Table` class determines what fields to serialize and in what order by checking each field with the `TableField` annotation. When deserializing an object (`Table.fromRow`), an empty object is created using the empty constructor, then each attribute on the object is set according to the annotated fields and their index. Additionally, there is a method to get an array of the column names for the class, which is used in `DatabaseWrapper` to initialized the `Database` objects.
- The tests use the `TestTable` class created specifically for tests. It tests that the object is correctly serialized and deserialized, checking against expected String arrays and TestTable objects. Additionally, it checks that the columns returned by `getColumns` are correct.
### TableField.java
- Ayden Cline
- Allows an annotation to define a field as being able to be put in a table, used for easy database operations.
### Message.java
- Ayden Cline
- This class extends `Table` and holds information about a message.
- The class holds information about who sent the message, who received it, the content, and when it was sent. Upon construction, the timestamp attribute is set to the current time, using `System.currentTimeMillis`. If an empty message is passed, an IllegalArgumentException is raised. The class also has getters and setters for each attribute.
- This class is used by the `User` class to send and receive messages.
- The tests for this class make sure that each getter and setter works as intended. It also tests that an IllegalArgumentException is thrown where it should be.
### User.java
- Chen Yang
- The User class is used to represent a user in the system. It stores and manages the user's details like their username, password, balance, and rating. The class also helps with managing the user's listings and messages.
- When a user is created, the User class requires a username and a password. Getters and setters are employed to update its fields (username, password, balance, and rating).
- The User class works closely with the Listing and Message classes, which store the actual items users want to sell and the messages they send. The DatabaseWrapper class is used to save and get all the user's data from the database.
- Testing for the User class makes sure that a user can be created with a valid username and password, that the balance and rating can be updated, and that listings and messages can be added and removed. It also checks that deleting the account works correctly and removes everything associated with the user.
### Listing.java
- Benny Huang
- The listing class sets and gets the values needed to create a lsting. It utilizes the "IListing" interface to create a set of methods that relate to listings. The class also extends the "Table" class, which allows Listing to use "id" that are obtained from methods like "getId()" and "setId()."
- Methods associated with the Listing class include: getSellerId(), setSellerId(), getSellerName(), setSellerName(String sellerName), getTitle(), setTitle(), getDescription(), setDescription(), getPrice(), setPrice(), isSold(), setSold(), toString().
- Testing associated with the Listing class mostly entails making sure the setters and getters return the right values when they are being used.
- All of this allows for the other classes, such as user or databaseWrapper classes, use the methods that are created in this listing class.
- Image support will be implemented through getImage() and setImage().
### Session.java
- Ayden Cline
- This class is used to track a client session so they can be verified as logged in between requests or even restarts.
- This class is used by the login handler and packet handlers that require the user to be logged in.
- Testing is implemented to ensure the class can be properly instantiated and its getters and setters function correctly.



## Database
### Database.java
- Karma Luitel
- This class provides 4 main operations and directly interacts with .csv files used as our database. It is initialized using a filename which will be used to reference the csv file it is using, and a set of String headers which refer to the columns of the database. The write method will take an array corresponding to a row of the csv file and append it to the next line of the csv file. The update method, which is overloaded, will take a header value pair to identify a specific line, and either update the value of another specified header or update the entire line, depending on which overloaded method is called. The get method will take in a header value pair identifying a line and return the original array representation of that line.  Finally the delete method will delete the line for a given header value pair.  All three (get, delete, update) methods will perform their actions on multiple lines if multiple header value pairs are found, and will also all throw a DatabaseNotFoundException if the given filename does not correspond to a valid database file. String sanitization is handled by modifying user input with quotes before writing and modifying before getting. Getters and setters also exist for the filename and header array, and all operations are thread safe. The associated interface is IDatabase.java, and the associated test file is TestBaseDatabase.java. Testing for the database class makes sure it can read, write, update, and delete lines correctly, and that it returns the correct error for an invalid database. The DatabaseWrapper class interfaces with Database to provide functionality for all other classes.
### DatabaseWrapper.java
- Ayden Cline
- This class wraps the `Database` class with higher-level functions that work with the `Table` class. The class contains methods for getting and filtering objects from the database, saving objects to the database, and deleting objects from the database.
- This is the sole class meant to be used for interacting with the database. Classes that need to use the database will call the static method `get` to get a global instance of the class and then use it as needed.
- When performing any database actions for a specific object, it first determines the class of the object and fetches the database for that class, creating a new one if it doesn't exist. When getting or filtering objects from the database, it calls `Database.get` to get the values, then maps the values to their respective objects via `Table.fromRow`. When saving an object, it first determines whether it needs to create the object or update the object. Objects with an id of 0 are assumed to not exist yet, so a new database row is created with `Database.write`, and otherwise it uses the id to update the row in the database with `Database.update`. The values to be added/updated to the database come from `Table.asRow`. When deleting an object, it simply calls `Database.delete` for the object's id, or does nothing if the id is 0 (object doesn't exist).
- The tests use the `TestTable` class created specifically for tests. The tests check that each method doesn't cause errors and that they take effect in the database. For example, after deleting an object, it attempts to get it from the database, ensuring it no longer exists. Additionally, there are tests to ensure intended behavior occurs for unexpected arguments and unintended function calls (e.g. calling `delete` twice). Lastly, there's a test that starts 5 threads at the same time, which each do heavy database work, and check everything was retrieved, saved, and deleted as intended.
### DatabaseNotFoundException.java
- Karma Luitel
- Exception for when a database is missing.
### DatabaseWriteException.java
- Ayden Cline
- An exception that handles a problematic database write.
### RowNotFoundException.java
- Ayden Cline
- A exception that handles a row not found error for the database.


## Packet
### Packet.java
- Ayden Cline
- This is the base class that carries information between server and client.
- Packets have a path, headers, and body, although all are optional.
  - The path tells the server where the packet is intended to be sent
  - Headers carry generally any information or data
  - The body is for binary data, such as files
- In addition, the class provides a method for writing to an output stream and reading from an input stream.
- This class is used by all the client handler, endpoint handlers, and the client.
- The tests are implemented to make sure the packet can be properly instantiated, the getters and setters function, and its read and write functions work correctly.
### PacketHeader.java
- Ayden Cline
- This class contains header information.
- Headers have one key, but can have multiple values.
- This class is used solely by the `Packet` class.
- The tests ensure that it can be properly instantiated and the getters and setters function correctly.
### SuccessPacket.java
- Ayden Cline
- This class extends `Packet` and adds a "Status" header indicating that the packet is not an error packet.
- The class is used by some handlers that don't return any objects.
- The tests ensure the status header is added and with the correct value.
### ErrorPacket.java
- Ayden Cline
- This class extends `Packet` and adds a "Status" header indicating that the packet is an error packet.
- The class is used by the client handler and all handlers in case of invalid data, an internal error, or some other reason.
- The tests ensure it's instantiated as expected, the status header is added with the correct value, and the getter and setter methods works.
### ObjectPacket.java
- Ayden Cline
- This class extends `SuccessPacket` with an attribute that holds an object of a class extending `Table`.
- The class is intended for sending database objects between the server and client, and is used by some handlers.
- The tests ensure it's instantiated as expected and the getter and setter methods works.
### ObjectListPacket.java
- Karma Luitel
- This class extends SuccessPacket to account for having a list of objects that extend Table. This allows a list to be send from the client to server.
- Testing associated with this class is done in TestResponsePacket - just test the getters and setters.
- This class is used by various handlers such as GetListingsFromAttributeHandler, GetMessagesBetweenUsersHandler, GetUsersFromAttributeHandler, and Client to handle sending data in a list format.
### ErrorPacketException.java
- Ayden Cline
- Thrown when an error packet is sent.
### PacketParsingException.java
- Ayden Cline
- Thrown when a packet is formatted incorrectly.



## Server
### Server.java
- Ayden Cline
- Starts the socket server and uses a thread pool via `Executors.newCachedThreadPool` to avoid the costs of creating a new thread for each client.
- Once a connection is made, the socket is handed off to `ClientHandler` to handle it, which runs in its own thread.
- The class is tested alongside the `Client` class to ensure it functions as intended and can handle multiple clients at once.
### ClientHandler.java
- Ayden Cline
- Implements `Runnable`, handling a single client connection.
- The run method continuously reads incoming packets from the client until it disconnects. Once a packet is received, it's sent to a packet handler depending on the path, or otherwise sends an error packet. If the packet has a body, it's read and stored to a file temporarily for the handler.
- The packet returned by the handler is then sent to the client.
- The class is tested alongside the `Client` class to ensure it handles all possible packets that can be received.
### PacketHandler.java
- Ayden Cline
- An abstract class that all packet handlers inherit from.
- The class implements a method for checking whether a provided path and the handler's path match. Additionally, it implements a wildcard syntax that can be used in the path. There is also a function that checks whether the packet comes from a user that is logged in and returns that user if so.
- The tests ensure that both methods do what they're intended to do.



## Server Handlers
### HandlerUtil.java
- Ayden Cline
- This class contains a few static methods that are utilities for some of the handler classes.
- It contains functionality for generating random token strings of varying length, hashing a string, and returning the hex string for a byte array.
- There are tests to ensure that each function returns the correct results.
### LoginHandler.java
- Ayden Cline
- This class extends `PacketHandler` and handles logging in a user.
- The client gives a username and password, and the handler checks whether they match what's in the database. If so, a new `Session` object is created, deleting any old ones associated with the user, and the session token is returned so the client's login can persist.
- The handler is tested to ensure it correctly returns a session token and error packets where valid.
### ImageUploadHandler
- Ayden Cline
- This class extends `PacketHandler` and handles image uploading.
- `ClientHandler` handles reading data from packet bodies to file, but as its temporary, this handler ensures it becomes permanent by including a hash of the file in the response header.
- If the user is not logged in or no body data was sent, an error packet is returned.
- The handler is tested to ensure the file is stored permanently and it returns an error packet where valid.
### ImageDownloadHandler
- Ayden Cline
- This class extends `PacketHandler` and handles sending an image file to the client.
- `ClientHandler` handles sending the packet bodies to the client, but the handler tells the handler whether to send a file and which one to send.
- An invalid hash requested from the client will result in an error packet being sent back.
- The handler is tested to ensure the file is properly marked to be returned by the client handler.
### BuyListingHandler.java
- Karma Luitel
- This class handles the calculations and database operations for a user buying a listing.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission. 
- Testing for all handler classes is done in TestEndpointHandlers - with error tests for buying something with an invalid balance or buying an already sold item.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### CreateListingHandler.java
- Karma Luitel
- This class handles the creation of a new listing, with checks for invalid listing prices.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers - with error tests for an invalid listing price.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### CreateMessageHandler.java
- Karma Luitel
- This class handles the creation of a new message between two users.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### CreateUserHandler.java
- Karma Luitel
- This class handles the creation of a new user - with checks for a username overlap.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers - with error tests for a username overlap.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### DeleteListingHandler.java
- Karma Luitel
- This class handles the deletion of a listing.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### DeleteUserHandler.java
- Karma Luitel
- This class handles the deletion of a user.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### EditListingHandler.java
- Karma Luitel
- This class handles the editing of a property of a listing, with specific checks for specific properties.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers. Has error tests for specific and invalid properties.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### EditUserHandler.java
- Karma Luitel
- This class handles the editing of a property of a user, with specific checks for specific properties.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers. Has error tests for specific and invalid properties.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### GetListingsFromAttributeHandler.java
- Karma Luitel
- This class handles getting a list of listings based on an attribute. For example, to get a list of listing with price 100, you would use this class with attribute "price" and attribute value "100".
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### GetMessagesBetweenUsersHandler.java
- Karma Luitel
- This class handles getting a list of all messages sent from one user to another user.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### GetUserFromIdHandler.java
- Karma Luitel
- This class handles getting a user from its id value.
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
### GetUsersFromAttributeHandler.java
- Karma Luitel
- This class handles getting a list of users based on an attribute. For example, to get a list of users with username "john", you would use this class with attribute "username" and attribute value "john" (would be a list of size 1).
- It implements the handle method from the abstract PacketHandler class (its parent).
- All handler classes use Packet or subclasses of Packet to handle data transmission.
- Testing for all handler classes is done in TestEndpointHandlers.
- Handlers are utilized by Client.java to perform database operations from the client over the server.
