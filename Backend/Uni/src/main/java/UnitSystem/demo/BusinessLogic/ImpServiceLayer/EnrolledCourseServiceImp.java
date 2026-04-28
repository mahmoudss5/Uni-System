package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Logs.AuditLog;
import UnitSystem.demo.Aspect.Security.CheckStudentPermission;
import UnitSystem.demo.Aspect.Security.CheckTeacherPermission;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.EnrolledCourseService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService;
import UnitSystem.demo.BusinessLogic.Mappers.EnrolledCoursesMapper;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.EnrolledCourse;
import UnitSystem.demo.DataAccessLayer.Entities.StudentPermissions;
import UnitSystem.demo.DataAccessLayer.Entities.TeacherPermissions;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.EnrolledCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class EnrolledCourseServiceImp implements EnrolledCourseService {

    private final EnrolledCourseRepository enrolledCourseRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final EnrolledCoursesMapper mapper;


    @Override
    @Cacheable(value = "enrollmentsCache", key = "'allEnrollments'")
    public List<EnrolledCourseResponse> getAllEnrolledCourses() {
        return enrolledCourseRepository.findAll().stream()
                .map(mapper::mapToEnrolledCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "enrollmentsCache", key = "'enrollmentsByStudent:' + #studentId")
    public List<EnrolledCourseResponse> getEnrolledCoursesByStudentId(Long studentId) {
        User student = userService.findUserById(studentId);
        return enrolledCourseRepository.findByStudent(student).stream()
                .map(mapper::mapToEnrolledCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "enrollmentsCache", key = "'enrollmentsByCourse:' + #courseId")
    public List<EnrolledCourseResponse> getEnrolledCoursesByCourseId(Long courseId) {
        Course course = courseService.getCourseEntityById(courseId);
        return enrolledCourseRepository.findByCourse(course).stream()
                .map(mapper::mapToEnrolledCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "enrollmentsCache", key = "'enrollmentById:' + #enrolledCourseId")
    public EnrolledCourseResponse getEnrolledCourseById(Long enrolledCourseId) {
        return enrolledCourseRepository.findById(enrolledCourseId)
                .map(mapper::mapToEnrolledCourseResponse)
                .orElseThrow(() -> new UnitSystem.demo.ExcHandler.Entites.ResourceNotFoundException("EnrolledCourse", enrolledCourseId));
    }

    @Override
    @CheckStudentPermission(StudentPermissions.course_register)
    @AuditLog
    @Caching(evict = {
            @CacheEvict(value = "enrollmentsCache", allEntries = true),
            @CacheEvict(value = "studentsCache", key = "'studentDetails:' + #enrolledCourseRequest.studentId"),
            @CacheEvict(value = "coursesCache", allEntries = true)
    })
    public EnrolledCourseResponse enrollStudentInCourse(EnrolledCourseRequest enrolledCourseRequest) {
        User student = userService.findUserById(enrolledCourseRequest.getStudentId());

        Course course = courseService.getCourseEntityById(enrolledCourseRequest.getCourseId());

        if (enrolledCourseRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        EnrolledCourse enrolledCourse = mapper.mapToEnrolledCourse(enrolledCourseRequest);
        enrolledCourseRepository.save(Objects.requireNonNull(enrolledCourse));
        return mapper.mapToEnrolledCourseResponse(enrolledCourse);
    }

    @Override
    @CheckTeacherPermission(TeacherPermissions.unenroll_student)
    @Caching(evict = {
            @CacheEvict(value = "enrollmentsCache", allEntries = true),
            @CacheEvict(value = "studentsCache", allEntries = true),
            @CacheEvict(value = "coursesCache", allEntries = true)
    })
    @Transactional
    @AuditLog
    public void unenrollStudentFromCourse(Long enrolledCourseId) {

        if (!enrolledCourseRepository.existsById(enrolledCourseId)) {
            throw new RuntimeException("Enrolled course not found with ID: " + enrolledCourseId);
        }
        enrolledCourseRepository.deleteByIdDirect(enrolledCourseId);
        log.info(" after the method end Attempting to unenroll student from course with enrollment ID: {}", enrolledCourseId);
    }

    @Override
    @TeachersOnly
    public EnrolledCourse findEnrolledCourseById(Long enrolledCourseId) {
        return enrolledCourseRepository.findById(enrolledCourseId)
                .orElseThrow(() -> new RuntimeException("Enrolled course not found"));
    }

    @Override
    public boolean isStudentEnrolledInCourse(Long studentId, Long courseId) {
        User student = userService.findUserById(studentId);
        Course course = courseService.getCourseEntityById(courseId);
        if (enrolledCourseRepository.existsByStudentAndCourse(student, course)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isStudentEnrolledInCourse(String useEmail, Long courseId) {
        User student = userService.findByEmail(useEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + useEmail));
        return isStudentEnrolledInCourse(student.getId(), courseId);

    }
}
