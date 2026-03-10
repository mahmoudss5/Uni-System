package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.StudentService;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Student.StudentRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Student.StudentResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UpcomingEvent.UpcomingEventResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.StudentDetailsResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.UserDetailsRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Announcement;
import UnitSystem.demo.DataAccessLayer.Entities.EnrolledCourse;
import UnitSystem.demo.DataAccessLayer.Entities.Role;
import UnitSystem.demo.DataAccessLayer.Entities.Student;
import UnitSystem.demo.DataAccessLayer.Entities.UpcomingEvent;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.AnnouncementRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.RoleRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.StudentRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UpcomingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImp implements StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AnnouncementRepository announcementRepository;
    private final UpcomingEventRepository upcomingEventRepository;

    private StudentResponse mapToStudentResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .userName(student.getUserName())
                .email(student.getEmail())
                .active(student.getActive())
                .createdAt(student.getCreatedAt())
                .gpa(student.getGpa())
                .enrollmentYear(student.getEnrollmentYear())
                .totalCredits(student.getTotalCredits())
                .roles(student.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .enrolledCoursesCount(student.getEnrolledCourses() != null ? student.getEnrolledCourses().size() : 0)
                .build();
    }

    private Student mapToStudent(StudentRequest studentRequest) {
        Set<Role> roles = studentRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        return Student.builder()
                .userName(studentRequest.getUserName())
                .email(studentRequest.getEmail())
                .password(passwordEncoder.encode(studentRequest.getPassword()))
                .active(studentRequest.getActive() != null ? studentRequest.getActive() : true)
                .gpa(studentRequest.getGpa())
                .enrollmentYear(studentRequest.getEnrollmentYear())
                .totalCredits(studentRequest.getTotalCredits())
                .roles(roles)
                .build();
    }

    @Override
    @Cacheable(value = "studentsCache", key = "'allStudents'")
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "studentsCache", key = "'studentById:' + #studentId")
    public StudentResponse getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .map(this::mapToStudentResponse)
                .orElse(null);
    }

    @Override
    @Cacheable(value = "studentsCache", key = "'studentByUserName:' + #userName")
    public StudentResponse getStudentByUserName(String userName) {
        Student student = studentRepository.findByUserName(userName);
        return student != null ? mapToStudentResponse(student) : null;
    }

    @Override
    @CacheEvict(value = "studentsCache", allEntries = true)
    public StudentResponse createStudent(StudentRequest studentRequest) {
        Student student = mapToStudent(studentRequest);
        studentRepository.save(student);
        return mapToStudentResponse(student);
    }

    @Override
    @CacheEvict(value = "studentsCache", allEntries = true)
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        existingStudent.setUserName(studentRequest.getUserName());
        existingStudent.setEmail(studentRequest.getEmail());
        if (studentRequest.getPassword() != null && !studentRequest.getPassword().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        }
        existingStudent.setActive(studentRequest.getActive());
        existingStudent.setGpa(studentRequest.getGpa());
        existingStudent.setEnrollmentYear(studentRequest.getEnrollmentYear());
        existingStudent.setTotalCredits(studentRequest.getTotalCredits());

        if (studentRequest.getRoles() != null) {
            Set<Role> roles = studentRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            existingStudent.setRoles(roles);
        }

        studentRepository.save(existingStudent);
        return mapToStudentResponse(existingStudent);
    }

    @Override
    public void saveUserAsStudent(User user) {
        Student student = (Student) user;
        studentRepository.save(student);
    }

    @Override
    @CacheEvict(value = "studentsCache", allEntries = true)
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }

    private EnrolledCourseResponse mapToEnrolledCourseResponse(EnrolledCourse enrolledCourse) {
        return EnrolledCourseResponse.builder()
                .id(enrolledCourse.getId())
                .studentId(enrolledCourse.getStudent().getId())
                .studentName(enrolledCourse.getStudent() instanceof Student s ? s.getUserName() : "")
                .courseId(enrolledCourse.getCourse().getId())
                .courseName(enrolledCourse.getCourse().getName())
                .enrollmentDate(enrolledCourse.getEnrollmentDate())
                .build();
    }

    private AnnouncementResponse mapToAnnouncementResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getDescription())
                .courseId(announcement.getCourse() != null ? announcement.getCourse().getId() : null)
                .createdDate(announcement.getCreatedAt())
                .build();
    }

    private UpcomingEventResponse mapToUpcomingEventResponse(UpcomingEvent event) {
        return UpcomingEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .subtitle(event.getSubtitle())
                .eventDate(event.getEventDate())
                .type(event.getType().name())
                .userId(event.getUser() != null ? event.getUser().getId() : null)
                .userName(event.getUser() != null ? event.getUser().getUserName() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }

    private String calculateAcademicStanding(BigDecimal gpa) {
        if (gpa == null)
            return "N/A";
        double g = gpa.doubleValue();
        if (g >= 3.5)
            return "Excellent";
        if (g >= 3.0)
            return "Very Good";
        if (g >= 2.5)
            return "Good";
        if (g >= 2.0)
            return "Satisfactory";
        return "Probation";
    }

    @Override
    @Cacheable(value = "studentsCache", key = "'studentDetails:' + #userDetailsRequest.userId")
    public StudentDetailsResponse getStudentDetails(UserDetailsRequest userDetailsRequest) {
        Student student = studentRepository.findById(userDetailsRequest.getUserId())
                .orElseThrow(
                        () -> new RuntimeException("Student not found with ID: " + userDetailsRequest.getUserId()));

        Set<EnrolledCourseResponse> enrolledCourses = student.getEnrolledCourses() != null
                ? student.getEnrolledCourses().stream()
                        .map(this::mapToEnrolledCourseResponse)
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        // Get all announcements from student's enrolled courses
        List<AnnouncementResponse> announcements = student.getEnrolledCourses() != null
                ? student.getEnrolledCourses().stream()
                        .flatMap(enrolledCourse -> announcementRepository.findByCourseId(enrolledCourse.getCourse().getId()).stream())
                        .map(this::mapToAnnouncementResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        // Get upcoming events for the student
        List<UpcomingEventResponse> upcomingEvents = upcomingEventRepository.findByUserId(student.getId())
                .stream()
                .map(this::mapToUpcomingEventResponse)
                .collect(Collectors.toList());

        return StudentDetailsResponse.builder()
                .id(student.getId())
                .username(student.getUserName())
                .email(student.getEmail())
                .gpa(student.getGpa())
                .enrollmentYear(student.getEnrollmentYear())
                .totalCredits((long) student.getTotalCredits())
                .enrolledCourses(enrolledCourses)
                .enrolledCoursesCount(enrolledCourses.size())
                .academicStanding(calculateAcademicStanding(student.getGpa()))
                .announcements(announcements)
                .upcomingEvents(upcomingEvents)
                .build();
    }
}
