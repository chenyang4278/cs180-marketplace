package data;

/**
 * Message
 * <p>
 * A class that defines and allows operations on a message
 * being sent from a user to another user.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class Message extends Table implements IMessage {
    @TableField(field = "senderId", index = 1)
    private int senderId;

    @TableField(field = "receiverId", index = 2)
    private int receiverId;

    @TableField(field = "message", index = 3)
    private String message;

    @TableField(field = "timestamp", index = 4)
    private long timestamp;

    // Required for Table
    public Message() {
    }

    public Message(int senderId, int receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    //general getters/setters for message information
    @Override
    public int getSenderId() {
        return senderId;
    }

    @Override
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    @Override
    public int getReceiverId() {
        return receiverId;
    }

    @Override
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}