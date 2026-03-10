package UnitSystem.demo.Aspect.Security;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.EnrolledCourseService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static UnitSystem.demo.Security.Util.SecurityUtils.getCurrentUserId;

@Component
@Slf4j
@RequiredArgsConstructor
@Aspect
public class SecurityAspect {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrolledCourseService enrolledCourseService;

    @Before("@annotation(UnitSystem.demo.Aspect.Security.TeachersOnly)")
    public void checkTeacherRole() {
        log.info("Checking if user has teacher role...");

     Long UserId= getCurrentUserId();
     if(UserId==null){
         throw new RuntimeException("Current user id is null");
     }
        User currentUser = userService.findUserById(UserId);
       boolean isTeacher = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("TEACHER"));

        if (!isTeacher) {
            log.warn("Access denied for user {}. User does not have TEACHER role.", currentUser.getUserName());
            throw new RuntimeException("Access denied: You must be a teacher to access this resource.");
        }
        log.info("User {} has TEACHER role. Access granted.", currentUser.getUserName());
    }


    @Before("@annotation(UnitSystem.demo.Aspect.Security.CourseTeacherOnly)")
    public void checkCourseTeacher(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        EnrolledCourseRequest request = (EnrolledCourseRequest) args[0];
        Long UserId = getCurrentUserId();
        if (UserId == null) {
            log.warn("Current user ID is null. Access denied.");
            throw new RuntimeException("Access denied: Current user ID is null.");
        }
        Long CourseId = request.getCourseId();
        Teacher courseTeacher = courseService.findCourseTeacher(CourseId);
        if (courseTeacher == null || !courseTeacher.getId().equals(UserId)) {
            log.warn("Access denied for user with ID {}. User is not the teacher of course with ID {}.", UserId, CourseId);
            throw new RuntimeException("Access denied: You must be the teacher of this course to access this resource.");
        }
        log.info("User with ID {} is the teacher of course with ID {}. Access granted.", UserId, CourseId);
    }
}
