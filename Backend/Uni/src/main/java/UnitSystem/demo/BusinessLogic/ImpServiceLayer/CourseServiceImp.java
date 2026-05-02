package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Logs.AuditLog;
import UnitSystem.demo.Aspect.Security.CourseTeacherOnly;
import UnitSystem.demo.Aspect.Security.CheckTeacherPermission;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.BusinessLogic.Mappers.CourseMapper;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import UnitSystem.demo.DataAccessLayer.Entities.Values.TeacherPermissions;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import UnitSystem.demo.ExcHandler.Entites.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImp implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @Cacheable(value = "coursesCache", key = "'allCourses'")
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'popularCourses_' + #topN")
    public List<CourseResponse> getMostPopularCourses(int topN) {
        return courseRepository.findTopPopularCourses().stream()
                .limit(topN)
                .map(courseMapper::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'courseById:' + #courseId")
    public CourseResponse getCourseById(Long courseId) {
        Long id = Objects.requireNonNull(courseId, "courseId cannot be null");
        log.info("Fetching course with ID: {}", courseId);
        return courseRepository.findById(id)
                .map(courseMapper::mapToCourseResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    @Override
    @TeachersOnly
    @CheckTeacherPermission(TeacherPermissions.create_course)
    @CacheEvict(value = "coursesCache", allEntries = true)
    @AuditLog
    public CourseResponse createCourse(CourseRequest courseRequest) {
        log.info("Creating course: {}", courseRequest);
        Course course = courseMapper.mapToCourse(courseRequest);
        courseRepository.save(Objects.requireNonNull(course));
        return courseMapper.mapToCourseResponse(course);
    }

    @Override
    @CourseTeacherOnly
    @CheckTeacherPermission(TeacherPermissions.update_course)
    @CacheEvict(value = "coursesCache", allEntries = true)
    public CourseResponse updateCourse(CourseRequest courseRequest, Long courseId) {
        log.info("Updating course: {}", courseRequest);
        Course course = courseMapper.mapToCourse(courseRequest);
        course.setId(courseId);
        courseRepository.save(course);
        return courseMapper.mapToCourseResponse(course);
    }

    @Override
    @CourseTeacherOnly
    @CheckTeacherPermission(TeacherPermissions.delete_course)
    @CacheEvict(value = "coursesCache", allEntries = true)
    @Transactional
    @AuditLog
    public void deleteCourse(Long courseId) {
        log.info("Deleting course: {}", courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        courseRepository.deletByIdDirect(courseId);
        log.info("After Deleted course: {}", courseId);
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'coursesByDepartment:' + #departmentName")
    public List<CourseResponse> getCoursesByDepartment(String departmentName) {
        return courseRepository.findByDepartmentName(departmentName).stream()
                .map(courseMapper::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "coursesCache", key = "'coursesByTeacherId:' + #teacherId")
    public List<CourseResponse> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(courseMapper::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Teacher findCourseTeacher(Long courseId) {
        Long id = Objects.requireNonNull(courseId, "courseId cannot be null");
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id))
                .getTeacher();
    }

    @Override
    public Course getCourseEntityById(Long courseId) {
        Long id = Objects.requireNonNull(courseId, "courseId cannot be null");
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    @Override
    public List<String> findStudentEmailsByCourseId(Long courseId) {
        return courseRepository.findStudentEmailsByCourseId(courseId);
    }



    @Override
    public List<String> findMissingPrerequisiteNames(Long targetCourseId, Long studentId) {
        return courseRepository.findMissingPrerequisiteNames(targetCourseId, studentId);
    }
}
