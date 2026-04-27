package UnitSystem.demo.BusinessLogic.Mappers;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Permission;
import UnitSystem.demo.DataAccessLayer.Entities.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionMapper {


    public PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }

    public UserPermissionResponse toUserPermissionResponse(UserPermission userPermission) {
        return UserPermissionResponse.builder()
                .userId(userPermission.getUser().getId())
                .permissionId(userPermission.getPermission().getId())
                .permissionName(userPermission.getPermission().getName())
                .granted(userPermission.isGranted())
                .build();
    }
}
