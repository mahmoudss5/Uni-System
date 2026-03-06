package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllCourses();
    List<CourseResponse> getMostPopularCourses(int topN);
    CourseResponse getCourseById(Long courseId);
    CourseResponse createCourse(CourseRequest courseRequest);
    CourseResponse updateCourse( CourseRequest courseRequest, Long courseId);
    void deleteCourse(Long courseId);
    List<CourseResponse>getCoursesByDepartment(String departmentName);
     List<CourseResponse> getCoursesByTeacherId(Long teacherId);


}
