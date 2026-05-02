package UnitSystem.demo.Controllers;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.EnrolledCourseService;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/enrolled-courses")
@Tag(name = "Enrolled Course", description = "Endpoints for course enrollment management")
@RequiredArgsConstructor
public class EnrolledCourseController {

    private final EnrolledCourseService enrolledCourseService;

    @Operation(summary = "Get all enrolled courses")
    @GetMapping
    public ResponseEntity<List<EnrolledCourseResponse>> getAllEnrolledCourses() {
        List<EnrolledCourseResponse> enrolledCourses = enrolledCourseService.getAllEnrolledCourses();
        return ResponseEntity.ok(enrolledCourses);
    }

    @Operation(summary = "Get enrolled courses by student ID")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrolledCourseResponse>> getEnrolledCoursesByStudentId(@PathVariable Long studentId) {
        List<EnrolledCourseResponse> enrolledCourses = enrolledCourseService.getEnrolledCoursesByStudentId(studentId);
        return ResponseEntity.ok(enrolledCourses);
    }

    @Operation(summary = "Get enrolled courses by course ID")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrolledCourseResponse>> getEnrolledCoursesByCourseId(@PathVariable Long courseId) {
        List<EnrolledCourseResponse> enrolledCourses = enrolledCourseService.getEnrolledCoursesByCourseId(courseId);
        return ResponseEntity.ok(enrolledCourses);
    }

    @Operation(summary = "Get enrolled course by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EnrolledCourseResponse> getEnrolledCourseById(@PathVariable Long id) {
        EnrolledCourseResponse enrolledCourse = enrolledCourseService.getEnrolledCourseById(id);
        if (enrolledCourse != null) {
            return ResponseEntity.ok(enrolledCourse);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Enroll student in a course")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EnrolledCourseResponse> enrollStudentInCourse(@Valid @RequestBody EnrolledCourseRequest enrolledCourseRequest) {
        EnrolledCourseResponse enrolledCourse = enrolledCourseService.enrollStudentInCourse(enrolledCourseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrolledCourse);
    }

    @Operation(summary = "Student unenrolls from own course")
    @DeleteMapping("/student/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> studentUnenrollFromCourse(@PathVariable Long id) {
        enrolledCourseService.studentUnenrollFromCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Teacher unenrolls any student from own course")
    @DeleteMapping("/teacher/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> teacherUnenrollStudentFromCourse(@PathVariable Long id) {
        log.info("Teacher unenroll request for enrollment ID: {}", id);
        enrolledCourseService.teacherUnenrollStudentFromCourse(id);
        return ResponseEntity.noContent().build();
    }
}
