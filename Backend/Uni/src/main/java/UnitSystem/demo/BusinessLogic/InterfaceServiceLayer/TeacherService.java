package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherResponse;

import java.util.List;

public interface TeacherService {
    List<TeacherResponse> getAllTeachers();

    TeacherResponse getTeacherById(Long teacherId);

    TeacherResponse getTeacherByUserName(String userName);

    TeacherResponse createTeacher(TeacherRequest teacherRequest);

    TeacherResponse updateTeacher(Long teacherId, TeacherRequest teacherRequest);

    void deleteTeacher(Long teacherId);
}
