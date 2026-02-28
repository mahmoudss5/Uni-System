package UnitSystem.demo.BusinessLogic.ImpServiceLayer;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.StudentService;
import UnitSystem.demo.DataAccessLayer.Dto.Student.StudentRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Student.StudentResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Role;
import UnitSystem.demo.DataAccessLayer.Entities.Student;
import UnitSystem.demo.DataAccessLayer.Repositories.RoleRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImp implements StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .map(this::mapToStudentResponse)
                .orElse(null);
    }

    @Override
    public StudentResponse getStudentByUserName(String userName) {
        Student student = studentRepository.findByUserName(userName);
        return student != null ? mapToStudentResponse(student) : null;
    }

    @Override
    public StudentResponse createStudent(StudentRequest studentRequest) {
        Student student = mapToStudent(studentRequest);
        studentRepository.save(student);
        return mapToStudentResponse(student);
    }

    @Override
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
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }
}
