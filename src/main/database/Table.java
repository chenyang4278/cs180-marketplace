package database;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Table
 * <p>
 * A class to be extended by classes of objects that are intended to be stored in the database.
 * Fields that are to be serialized and stored should be denoted by using the
 * TableField annotation and specifying the field name and index.
 * Index specification should start at 1 (id is index 0) and go up for each field.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
// abstract because this class should always be extended, never used directly
public abstract class Table implements ITable {
    @TableField(field = "id", index = 0)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    static private Object parseFieldValue(Field field, String value) {
        // parse string value back to its original value type
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

        throw new RuntimeException("Invalid table field type: " + field.getType());
    }

    static private <T extends Table> Stream<Field> getFields(Class<T> cls) {
        // get all fields declared in the class
        // then add id field from this class
        Field[] fields = cls.getDeclaredFields();
        fields = Arrays.copyOf(fields, fields.length + 1);
        try {
            fields[fields.length - 1] = Table.class.getDeclaredField("id");
        } catch (NoSuchFieldException e) { // unreachable
            throw new RuntimeException(e);
        }
        // return fields that are annotated by TableField
        // and sort by their specified index
        return Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(TableField.class))
                .sorted((a, b) -> {
                    var annotationA = a.getAnnotation(TableField.class);
                    var annotationB = b.getAnnotation(TableField.class);
                    return annotationA.index() - annotationB.index();
                });
    }

    /**
     * Get the column names of a class that extends Table
     *
     * @param cls class for which to get column names
     * @param <T> class that extends Table
     * @return array of column names
     */
    static public <T extends Table> String[] getColumns(Class<T> cls) {
        // map list of annotated fields to their specified field names
        return getFields(cls)
                .map(f -> f.getAnnotation(TableField.class).field())
                .toArray(String[]::new);
    }

    /**
     * Creates an object from serialized values
     *
     * @param cls Class to create an object of
     * @param row List of serialized values
     * @param <T> class that extends Table and matches cls argument
     * @return a new object with attributes set to the unserialized values
     */
    static public <T extends Table> T fromRow(Class<T> cls, String[] row) {
        Field[] fields = getFields(cls).toArray(Field[]::new);

        try {
            // create new blank object
            Object obj = cls.getConstructor().newInstance();

            for (Field field : fields) {
                // get annotation info for this field
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation != null) {
                    // set the field to be accessible
                    field.setAccessible(true);
                    // then initiate its value
                    field.set(obj, parseFieldValue(field, row[annotation.index()]));
                    // return it back to false
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
            // map each field to its value in this object
            return getFields(this.getClass())
                    .map(f -> {
                        try {  // java makes me put this try statement inside the stream
                            f.setAccessible(true);
                            String value = String.valueOf(f.get(this));
                            f.setAccessible(false);
                            return value;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(String[]::new);
        } catch (Exception e) {  // shouldn't occur unless maybe the classes are in different modules for some reason
            System.out.println("Failed to convert " + this.getClass().getSimpleName());
            throw new RuntimeException(e);
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

    /**
     * Deletes the object from the database
     *
     * @throws DatabaseWriteException
     */
    public void delete() throws DatabaseWriteException {
        DatabaseWrapper.get().delete(this);
    }
}
