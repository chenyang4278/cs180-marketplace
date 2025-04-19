package data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TableField
 * <p>
 * A class that allows an annotation to define a field as being able to be put in a table,
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
