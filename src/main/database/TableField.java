package database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * SerializableField
 * <p>
 * A class that allows an annotation to define a field as Serializable,
 * used for easy database operations.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    String field();
    int index();
}
