import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class Serializable implements ISerializable {
    @SerializableField( field = "id", index = 0 )
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Serializable() {
        id = 0;
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

    static public <T extends Serializable> String[] getColumns(Class<T> cls) {
        return getFields(cls)
            .map(f -> f.getAnnotation(SerializableField.class).field())
            .toArray(String[]::new);
    }

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
        } catch (Exception e) { // shouldn't occur
            System.out.println("Failed to create " + cls.getName() + " from row");
            throw new RuntimeException(e);
        }
    }

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

    public void save() throws DatabaseWriteException {
        DatabaseWrapper.get().save(this);
    }
}
