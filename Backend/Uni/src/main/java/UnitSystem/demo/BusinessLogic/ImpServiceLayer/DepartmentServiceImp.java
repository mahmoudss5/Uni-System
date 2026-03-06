package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.DepartmentService;
import UnitSystem.demo.DataAccessLayer.Dto.Department.DepartmentRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Department.DepartmentResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Department;
import UnitSystem.demo.DataAccessLayer.Repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImp implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private DepartmentResponse mapToDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    private Department mapToDepartment(DepartmentRequest departmentRequest) {
        Department department = new Department();
        department.setName(departmentRequest.getName());
        return department;
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDepartmentResponse)
                .toList();
    }

    @Override
    public DepartmentResponse getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .map(this::mapToDepartmentResponse)
                .orElse(null);
    }

    @Override
    public DepartmentResponse getDepartmentByName(String name) {
        Department department = departmentRepository.findByName(name);
        return department != null ? mapToDepartmentResponse(department) : null;
    }

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        Department department = mapToDepartment(departmentRequest);
        departmentRepository.save(department);
        return mapToDepartmentResponse(department);
    }

    @Override
    public DepartmentResponse updateDepartment(Long departmentId, DepartmentRequest departmentRequest) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existingDepartment.setName(departmentRequest.getName());
        departmentRepository.save(existingDepartment);
        return mapToDepartmentResponse(existingDepartment);
    }

    @Override
    public void deleteDepartment(Long departmentId) {
        departmentRepository.deleteById(departmentId);
    }

    @Override
    public Boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }
}
