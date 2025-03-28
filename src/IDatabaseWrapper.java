import java.util.List;

public interface IDatabaseWrapper {
    <T extends Serializable> T getByColumn(Class<T> cls, String column, String value) throws RowNotFoundException;
    <T extends Serializable> List<T> filterByColumn(Class<T> cls, String column, String value) throws RowNotFoundException;
    <T extends Serializable> T getById(Class<T> cls, int id) throws RowNotFoundException;
    <T extends Serializable> void save(T obj) throws DatabaseWriteException;
}
