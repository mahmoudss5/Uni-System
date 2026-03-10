package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Security.CourseTeacherOnly;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.Department;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.DepartmentRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.TeacherRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
                .description(course.getDescription())
                .courseCode(course.getCourseCode())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
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
                .courseCode(courseRequest.getCourseCode())
                .StartDate(courseRequest.getStartDate())
                .EndDate(courseRequest.getEndDate())
                .Credits(courseRequest.getCreditHours())
                .Capacity(courseRequest.getMaxStudents())
                .department(departmentRepository.findByName(courseRequest.getDepartmentName()))
                .teacher(teacherRepository.findByUserName(courseRequest.getTeacherUserName()))
                .build();
    }


   @Override
   @Cacheable(value = "coursesCache", key = "'allCourses'")
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override @Cacheable(value = "coursesCache", key = "'popularCourses_' + #topN")
    public List<CourseResponse> getMostPopularCourses(int topN) {
        return courseRepository.findTopPopularCourses().stream()
                .limit(topN)
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'courseById:' + #courseId")
    public CourseResponse getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .map(this::mapToCourseResponse)
                .orElse(null);
    }

    @Override
    @TeachersOnly
    @CacheEvict(value = "coursesCache", allEntries = true)
    public CourseResponse createCourse(CourseRequest courseRequest) {
        log.info("Creating course " + courseRequest);
       Course course = mapToCourse(courseRequest);
        courseRepository.save(course);
       return mapToCourseResponse(course);
    }

    @Override
    @CourseTeacherOnly
    @CacheEvict(value = "coursesCache", allEntries = true)
    public CourseResponse updateCourse( CourseRequest courseRequest, Long courseId) {
       log.info("Updating course " + courseRequest);
       Course course = mapToCourse(courseRequest);
        course.setId(courseId);
        courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Override
    @CourseTeacherOnly
    @CacheEvict(value = "coursesCache", allEntries = true)
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
        
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'coursesByDepartment:' + #departmentName")
    public List<CourseResponse> getCoursesByDepartment(String departmentName) {
       Department department = departmentRepository.findByName(departmentName);
        return courseRepository.findByDepartment(department).stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'coursesByTeacherId:' + #teacherId")
    public List<CourseResponse> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Teacher findCourseTeacher(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getTeacher();
    }

    @Override
    public Course getCourseEntityById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}
