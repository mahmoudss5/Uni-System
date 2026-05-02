package UnitSystem.demo.BusinessLogic.Mappers;

import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.EnrolledCourse;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EnrolledCoursesMapper {

    public EnrolledCourseResponse mapToEnrolledCourseResponse(EnrolledCourse enrolledCourse) {
        Course course = enrolledCourse.getCourse();
        return EnrolledCourseResponse.builder()
                .id(enrolledCourse.getId())
                .studentId(enrolledCourse.getStudent().getId())
                .studentName(enrolledCourse.getStudent().getUserName())
                .courseId(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getName())
                .teacherName(course.getTeacher() != null ? course.getTeacher().getUserName() : null)
                .credits((long) course.getCredits())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .enrollmentDate(enrolledCourse.getEnrollmentDate())
                .build();
    }
    public EnrolledCourse mapToEnrolledCourse(User student ,Course course) {
        return EnrolledCourse.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDateTime.now())
                .build();
    }

}
