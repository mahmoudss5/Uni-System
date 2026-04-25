package UnitSystem.demo.Aspect.Security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requests() default 20;        // max calls
    int perSeconds() default 60;      // in this window
    String key() default "";          // optional label
}