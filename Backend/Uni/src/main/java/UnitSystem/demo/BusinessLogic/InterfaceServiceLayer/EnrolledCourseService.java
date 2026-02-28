package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;

import java.util.List;

public interface EnrolledCourseService {
    List<EnrolledCourseResponse> getAllEnrolledCourses();

    List<EnrolledCourseResponse> getEnrolledCoursesByStudentId(Long studentId);

    List<EnrolledCourseResponse> getEnrolledCoursesByCourseId(Long courseId);

    EnrolledCourseResponse getEnrolledCourseById(Long enrolledCourseId);

    EnrolledCourseResponse enrollStudentInCourse(EnrolledCourseRequest enrolledCourseRequest);

    void unenrollStudentFromCourse(Long enrolledCourseId);
}
