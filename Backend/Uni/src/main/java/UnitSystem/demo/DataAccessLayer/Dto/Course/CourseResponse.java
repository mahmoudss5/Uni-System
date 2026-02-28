package UnitSystem.demo.DataAccessLayer.Dto.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private String departmentName;
    private String teacherUserName;
    private int creditHours;
    private int maxStudents;
    private int enrolledStudents;
}
