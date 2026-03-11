package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Security.CourseTeacherOnly;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.EnrolledCourseService;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.EnrolledCourse;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.EnrolledCourseRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import UnitSystem.demo.ExcHandler.Entites.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrolledCourseServiceImp implements EnrolledCourseService {

        private final EnrolledCourseRepository enrolledCourseRepository;
        private final UserRepository userRepository;
        private final CourseRepository courseRepository;

        private EnrolledCourseResponse mapToEnrolledCourseResponse(EnrolledCourse enrolledCourse) {
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

        private EnrolledCourse mapToEnrolledCourse(EnrolledCourseRequest enrolledCourseRequest) {
                User student = userRepository.findById(enrolledCourseRequest.getStudentId())
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                Course course = courseRepository.findById(enrolledCourseRequest.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found"));

                return EnrolledCourse.builder()
                                .student(student)
                                .course(course)
                                .enrollmentDate(LocalDateTime.now())
                                .build();
        }

        @Override
        @Cacheable(value = "enrollmentsCache", key = "'allEnrollments'")
        public List<EnrolledCourseResponse> getAllEnrolledCourses() {
                return enrolledCourseRepository.findAll().stream()
                                .map(this::mapToEnrolledCourseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Cacheable(value = "enrollmentsCache", key = "'enrollmentsByStudent:' + #studentId")
        public List<EnrolledCourseResponse> getEnrolledCoursesByStudentId(Long studentId) {
                User student = userRepository.findById(studentId)
                                .orElseThrow(() -> new RuntimeException("Student not found"));
                return enrolledCourseRepository.findByStudent(student).stream()
                                .map(this::mapToEnrolledCourseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Cacheable(value = "enrollmentsCache", key = "'enrollmentsByCourse:' + #courseId")
        public List<EnrolledCourseResponse> getEnrolledCoursesByCourseId(Long courseId) {
                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found"));
                return enrolledCourseRepository.findByCourse(course).stream()
                                .map(this::mapToEnrolledCourseResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Cacheable(value = "enrollmentsCache", key = "'enrollmentById:' + #enrolledCourseId")
        public EnrolledCourseResponse getEnrolledCourseById(Long enrolledCourseId) {
                return enrolledCourseRepository.findById(enrolledCourseId)
                                .map(this::mapToEnrolledCourseResponse)
                                .orElse(null);
        }

        @Override
        @Caching(evict = {
                @CacheEvict(value = "enrollmentsCache", allEntries = true),
                @CacheEvict(value = "studentsCache", key = "'studentDetails:' + #enrolledCourseRequest.studentId")
        })
        public EnrolledCourseResponse enrollStudentInCourse(EnrolledCourseRequest enrolledCourseRequest) {
                User student = userRepository.findById(enrolledCourseRequest.getStudentId())
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                Course course = courseRepository.findById(enrolledCourseRequest.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found"));

                if (enrolledCourseRepository.existsByStudentAndCourse(student, course)) {
                        throw new RuntimeException("Student is already enrolled in this course");
                }

                EnrolledCourse enrolledCourse = mapToEnrolledCourse(enrolledCourseRequest);
                enrolledCourseRepository.save(enrolledCourse);
                return mapToEnrolledCourseResponse(enrolledCourse);
        }

        @Override
        @Caching(evict = {
                @CacheEvict(value = "enrollmentsCache", allEntries = true),
                @CacheEvict(value = "studentsCache", allEntries = true)
        })
        public void unenrollStudentFromCourse(Long enrolledCourseId) {
                enrolledCourseRepository.deleteById(enrolledCourseId);
        }

        @Override
        @TeachersOnly
        public EnrolledCourse findEnrolledCourseById(Long enrolledCourseId) {
                return enrolledCourseRepository.findById(enrolledCourseId)
                                .orElseThrow(() -> new RuntimeException("Enrolled course not found"));
        }
}
