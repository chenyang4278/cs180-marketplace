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
