package UnitSystem.demo.Aspect.Security;

import UnitSystem.demo.DataAccessLayer.Entities.TeacherPermissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTeacherPermission {
    TeacherPermissions value();
}
