package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.SalaryDto;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.TeacherDetailsResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.UserDetailsRequest;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;

import java.util.List;

public interface TeacherService {
    List<TeacherResponse> getAllTeachers();

    TeacherResponse getTeacherById(Long teacherId);

    TeacherResponse getTeacherByUserName(String userName);

    TeacherResponse createTeacher(TeacherRequest teacherRequest);

    TeacherResponse updateTeacher(Long teacherId, TeacherRequest teacherRequest);

    void saveUserASTeacher(Teacher teacher);

    void deleteTeacher(Long teacherId);

    TeacherDetailsResponse getTeacherDetails(UserDetailsRequest userDetailsRequest);
    SalaryDto getTeacherSalary(Long teacherId);
    Teacher findTeacherEntityById(Long teacherId);
}
