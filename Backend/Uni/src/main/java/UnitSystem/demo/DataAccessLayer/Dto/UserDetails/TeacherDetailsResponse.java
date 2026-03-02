package UnitSystem.demo.DataAccessLayer.Dto.UserDetails;

import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Data
public class TeacherDetailsResponse {
    private Long teacherId;
    private String name;
    private String email;
    private String department;
    private BigDecimal salary;
    private Set<String> roles;
    private Set<CourseResponse> courses;
    private int coursesCount;
    private Long numberOfStudents;
}
