package main.database;

/**
 * IMessage
 * <p>
 * A interface for a Message class.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public interface IMessage {
    int getSenderId();

    void setSenderId(int senderId);

    int getReceiverId();

    void setReceiverId(int receiverId);

    String getMessage();

    void setMessage(String message);

    long getTimestamp();

    void setTimestamp(long timestamp);
}
