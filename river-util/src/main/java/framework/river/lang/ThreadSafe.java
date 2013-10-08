package framework.river.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Annotation used to document that a class is thread safe, as well as to capture
 * a brief explanation as to how the class is thread safe.
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {

    public ThreadStrategy strategy();

    public String description();

}
