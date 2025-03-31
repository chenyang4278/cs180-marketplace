import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Serializable
 * <p>
 * A class to be extended by classes of objects that are intended to be stored in the database.
 * Fields that are to be serialized and stored should be denoted by using the
 * SerializableField annotation and specifying the field name and index.
 * Index specification should start at 1 (id is index 0) and go up for each field.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
// abstract because this class should always be extended, never used directly
public abstract class Serializable implements ISerializable {
    @SerializableField( field = "id", index = 0 )
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    static private Object parseFieldValue(Field field, String value) {
        if (field.getType().equals(String.class)) {
            return value;
        } else if (field.getType().equals(int.class)) {
            return Integer.parseInt(value);
        } else if (field.getType().equals(long.class)) {
            return Long.parseLong(value);
        } else if (field.getType().equals(double.class)) {
            return Double.parseDouble(value);
        } else if (field.getType().equals(float.class)) {
            return Float.parseFloat(value);
        }

        throw new RuntimeException("Unserializable field type: " + field.getType());
    }

    static private <T extends Serializable> Stream<Field> getFields(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        fields = Arrays.copyOf(fields, fields.length + 1);
        try {
            fields[fields.length - 1] = Serializable.class.getDeclaredField("id");
        } catch (NoSuchFieldException e) { // unreachable
            throw new RuntimeException(e);
        }
        return Arrays.stream(fields)
            .filter(f -> f.isAnnotationPresent(SerializableField.class))
            .sorted((a, b) -> {
                var annotationA = a.getAnnotation(SerializableField.class);
                var annotationB = b.getAnnotation(SerializableField.class);
                return annotationA.index() - annotationB.index();
            });
    }

    /**
     * Get the column names of a class that extends Serializable
     *
     * @param cls class for which to get column names
     * @return array of column names
     * @param <T> class that extends Serializable
     */
    static public <T extends Serializable> String[] getColumns(Class<T> cls) {
        return getFields(cls)
            .map(f -> f.getAnnotation(SerializableField.class).field())
            .toArray(String[]::new);
    }

    /**
     * Creates an object from serialized values
     *
     * @param cls Class to create an object of
     * @param row List of serialized values
     * @return a new object with attributes set to the unserialized values
     * @param <T> class that extends Serializable and matches cls argument
     */
    static public <T extends Serializable> T fromRow(Class<T> cls, String[] row) {
        Field[] fields = getFields(cls).toArray(Field[]::new);

        try {
            Object obj = cls.getConstructor().newInstance();

            for (Field field : fields) {
                SerializableField annotation = field.getAnnotation(SerializableField.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    field.set(obj, parseFieldValue(field, row[annotation.index()]));
                    field.setAccessible(false);
                }
            }

            return (T) obj;
        } catch (Exception e) { // will occur if there's no empty constructor
            System.out.println(
                "Failed to create " + cls.getName() + " from row. Make sure the class has an empty constructor."
            );
            throw new RuntimeException(e);
        }
    }

    /**
     * @return serialized values of this object
     */
    public String[] asRow() {
        try {
            return getFields(this.getClass())
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        String value = String.valueOf(f.get(this));
                        f.setAccessible(false);
                        return value;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(String[]::new);
        } catch (Exception e) {
            System.out.println("Failed to convert " + this.getClass().getSimpleName() + " ");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the object to the database
     * Call this method after making any changes to attributes.
     *
     * @throws DatabaseWriteException
     */
    public void save() throws DatabaseWriteException {
        DatabaseWrapper.get().save(this);
    }
}
