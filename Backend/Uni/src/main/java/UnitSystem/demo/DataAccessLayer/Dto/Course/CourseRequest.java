package UnitSystem.demo.DataAccessLayer.Dto.Course;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    private String name;
    private String description;
    private String departmentName;
    private String teacherUserName;
    private int creditHours;
    private int maxStudents;

}
