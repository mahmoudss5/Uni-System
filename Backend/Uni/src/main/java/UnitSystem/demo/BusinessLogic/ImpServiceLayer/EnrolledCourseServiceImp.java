package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Logs.AuditLog;
import UnitSystem.demo.Aspect.Security.CheckStudentPermission;
import UnitSystem.demo.Aspect.Security.CheckTeacherPermission;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.Aspect.pereformance.PerformanceMonitor;
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
import UnitSystem.demo.ExcHandler.Entites.MissingPrerequisitesException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class EnrolledCourseServiceImp implements EnrolledCourseService {

    /** Popular course endpoints allow arbitrary {@code topN}; evict frequent values explicitly. */
    private static final int[] POPULAR_COURSE_TOP_NS = {3, 4, 5, 10, 20, 50};

    private final EnrolledCourseRepository enrolledCourseRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final EnrolledCoursesMapper mapper;
    private final CacheManager cacheManager;

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
    @PerformanceMonitor
    public EnrolledCourseResponse enrollStudentInCourse(EnrolledCourseRequest enrolledCourseRequest) {
        User student = userService.findUserById(enrolledCourseRequest.getStudentId());

        Course course = courseService.getCourseEntityById(enrolledCourseRequest.getCourseId());

        if (enrolledCourseRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }
        List<String>preNeed=courseService.findMissingPrerequisiteNames(enrolledCourseRequest.getCourseId(),enrolledCourseRequest.getStudentId());
        if(!preNeed.isEmpty()){
            throw new MissingPrerequisitesException("Student is missing prerequisites for this course: ",preNeed);
        }
        EnrolledCourse enrolledCourse = mapper.mapToEnrolledCourse(enrolledCourseRequest);
        enrolledCourseRepository.save(Objects.requireNonNull(enrolledCourse));
        evictAfterEnrollmentMutation(
                enrolledCourseRequest.getStudentId(),
                enrolledCourseRequest.getCourseId(),
                enrolledCourse.getId(),
                course
        );
        return mapper.mapToEnrolledCourseResponse(enrolledCourse);
    }

    @Override
    @CheckTeacherPermission(TeacherPermissions.unenroll_student)
    @Transactional
    @AuditLog
    @PerformanceMonitor
    public void unenrollStudentFromCourse(Long enrolledCourseId) {

        EnrolledCourse enrollment = enrolledCourseRepository.findById(enrolledCourseId)
                .orElseThrow(() -> new RuntimeException("Enrolled course not found with ID: " + enrolledCourseId));
        Long studentId = enrollment.getStudent().getId();
        Long courseId = enrollment.getCourse().getId();
        Course course = enrollment.getCourse();
        enrolledCourseRepository.deleteByIdDirect(enrolledCourseId);
        evictAfterEnrollmentMutation(studentId, courseId, enrolledCourseId, course);
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


private List<String>getMissedPrerequisitesCoursesNames(List<Long> courseIds) {
        List<String> missedPrerequisites = courseService.getCoursesNamesByCourseIds(courseIds);
        return missedPrerequisites;
}
    private boolean checkCoursePrerequisites(Long courseId, Long studentId) {
        List<Long> preId=courseService.getCoursePrerequisites(courseId);
        if(preId==null||preId.isEmpty()){
            return true;
        }
        List<Long> enrollmentsCoursesIds=enrolledCourseRepository.findAllIdByStudentId(studentId);
        Set<Long>StudentEnrolledCoursesIds=new HashSet<>(enrollmentsCoursesIds);
        for(Long id:preId){
            if(StudentEnrolledCoursesIds.contains(id)){
                preId.remove(id);
            }
        }
        if(!preId.isEmpty()){
            throw new MissingPrerequisitesException("Student is missing prerequisites for this course: ",getMissedPrerequisitesCoursesNames(preId));
        }
        return true;
    }

    private static void safeEvict(Cache cache, Object key) {
        if (cache != null && key != null) {
            cache.evict(key);
        }
    }

    /**
     * Invalidates caches that can become stale when a student enrolls or unenrolls.
     * Avoids {@linkCacheEvict @CacheEvict}{@code allEntries}; clearing whole Redis-backed regions is slower.
     */
    private void evictAfterEnrollmentMutation(Long studentId, Long courseId, Long enrollmentId, Course course) {
        Cache enrollmentsCache = cacheManager.getCache("enrollmentsCache");
        safeEvict(enrollmentsCache, "allEnrollments");
        safeEvict(enrollmentsCache, "enrollmentsByStudent:" + studentId);
        safeEvict(enrollmentsCache, "enrollmentsByCourse:" + courseId);
        if (enrollmentId != null) {
            safeEvict(enrollmentsCache, "enrollmentById:" + enrollmentId);
        }

        Cache studentsCache = cacheManager.getCache("studentsCache");
        safeEvict(studentsCache, "studentDetails:" + studentId);
        safeEvict(studentsCache, "studentById:" + studentId);
        safeEvict(studentsCache, "allStudents");

        Cache coursesCache = cacheManager.getCache("coursesCache");
        safeEvict(coursesCache, "allCourses");
        safeEvict(coursesCache, "courseById:" + courseId);
        for (int topN : POPULAR_COURSE_TOP_NS) {
            safeEvict(coursesCache, "popularCourses_" + topN);
        }
        if (course != null) {
            if (course.getDepartment() != null && course.getDepartment().getName() != null) {
                safeEvict(coursesCache, "coursesByDepartment:" + course.getDepartment().getName());
            }
            if (course.getTeacher() != null) {
                safeEvict(coursesCache, "coursesByTeacherId:" + course.getTeacher().getId());
            }
        }

        Cache announcementsCache = cacheManager.getCache("announcementsCache");
        safeEvict(announcementsCache, "allAnnouncements");
        safeEvict(announcementsCache, "announcementsByCourse:" + courseId);
        safeEvict(announcementsCache, "announcementsByStudent:" + studentId);
        if (course != null && course.getTeacher() != null) {
            safeEvict(announcementsCache, "announcementsByTeacher:" + course.getTeacher().getId());
        }
    }
}
