package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.Department;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.DepartmentRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.TeacherRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImp implements CourseService {

   private final CourseRepository courseRepository;
   private final DepartmentRepository departmentRepository;
   private final TeacherRepository teacherRepository;



   private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .creditHours(course.getCredits())
                .enrolledStudents(course.getCourseEnrollments().size())
                .maxStudents(course.getCapacity())
                .departmentName(course.getDepartment().getName())
                .teacherUserName(course.getTeacher().getUserName())
                .build();
    }

    private Course mapToCourse(CourseRequest courseRequest) {
        return Course.builder()
                .name(courseRequest.getName())
                .description(courseRequest.getDescription())
                .Credits(courseRequest.getCreditHours())
                .Capacity(courseRequest.getMaxStudents())
                .department(departmentRepository.findByName(courseRequest.getDepartmentName()))
                .teacher(teacherRepository.findByUserName(courseRequest.getTeacherUserName()))
                .build();
    }


   @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseResponse> getMostPopularCourses(int topN) {
        return courseRepository.findTopPopularCourses().stream()
                .limit(topN)
                .map(this::mapToCourseResponse)
                .toList();
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .map(this::mapToCourseResponse)
                .orElse(null);
    }

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
       Course course = mapToCourse(courseRequest);
        courseRepository.save(course);
       return mapToCourseResponse(course);
    }

    @Override
    public CourseResponse updateCourse( CourseRequest courseRequest) {
       Course course = mapToCourse(courseRequest);
        courseRepository.save(course);

        return mapToCourseResponse(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
        
    }
}
