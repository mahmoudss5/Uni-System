package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;

import java.util.List;

public interface PermissionService {

    List<PermissionResponse> getAllPermissions();

    PermissionResponse getPermissionById(Long permissionId);

    PermissionResponse createPermission(PermissionRequest request);

    PermissionResponse updatePermission(Long permissionId, PermissionRequest request);

    void deletePermission(Long permissionId);

    UserPermissionResponse assignPermissionToUser(UserPermissionRequest request);

    void removePermissionFromUser(Long userId, Long permissionId);

    List<UserPermissionResponse> getUserPermissions(Long userId);
}
