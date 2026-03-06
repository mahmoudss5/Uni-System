package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Department.DepartmentRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Department.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long departmentId);

    DepartmentResponse getDepartmentByName(String name);

    DepartmentResponse createDepartment(DepartmentRequest departmentRequest);

    DepartmentResponse updateDepartment(Long departmentId, DepartmentRequest departmentRequest);

    void deleteDepartment(Long departmentId);
    Boolean existsByName(String name);
}
