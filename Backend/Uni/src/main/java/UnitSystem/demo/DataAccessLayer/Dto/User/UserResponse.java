package UnitSystem.demo.DataAccessLayer.Dto.User;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Role.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private  String username;
    private  String email;
    private  Boolean active;
    private Set<RoleResponse> roles;
    /** Explicit rows from {@code user_permissions} (both granted and denied overrides). */
    private List<UserPermissionResponse> userPermissions;
}
