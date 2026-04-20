package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;

import java.util.List;
import java.util.Map;

public interface PermissionService {

    List<PermissionResponse> getAllPermissions();

    PermissionResponse getPermissionById(Long permissionId);

    PermissionResponse createPermission(PermissionRequest request);

    PermissionResponse updatePermission(Long permissionId, PermissionRequest request);

    void deletePermission(Long permissionId);

    UserPermissionResponse assignPermissionToUser(UserPermissionRequest request);
    List<UserPermissionResponse> getUserPermissions(Long userId);

    /**
     * All explicit user-permission rows for the given users (granted or denied), grouped by user id.
     */
    Map<Long, List<UserPermissionResponse>> getUserPermissionsForUsers(List<Long> userIds);
    void revokePermissionFromUser(Long userId, Long permissionId);
    void resetUserPermissionOverride(Long userId, Long permissionId);
    void preventUserFromAccessingPermission(Long userId, Long permissionId);
}
