package alainvanhout.optionalrest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestEntityDefinition {
    String entityScope() default "";

    String instanceScope() default "";

    String entityName() default "";

    Class instanceClass() default Void.class;
}
