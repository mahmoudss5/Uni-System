package UnitSystem.demo.DataAccessLayer.Dto.UserDetails;

import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class StudentDetailsResponse {

    private Long id;
    private String email;
    private String username;
    private BigDecimal gpa;
    private Long totalCredits;
    private Set<EnrolledCourseResponse> enrolledCourses;
    private int enrolledCoursesCount;
    private int enrollmentYear;
    private String academicStanding;
}
