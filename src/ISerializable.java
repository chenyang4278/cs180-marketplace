public interface ISerializable {
    int getId();
    void setId(int id);
    String[] asRow();
    void save() throws DatabaseWriteException;
}
