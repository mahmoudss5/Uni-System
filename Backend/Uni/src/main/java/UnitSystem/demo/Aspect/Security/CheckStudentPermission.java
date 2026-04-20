package UnitSystem.demo.Aspect.Security;

import UnitSystem.demo.DataAccessLayer.Entities.StudentPermissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckStudentPermission {
    StudentPermissions value();
}
