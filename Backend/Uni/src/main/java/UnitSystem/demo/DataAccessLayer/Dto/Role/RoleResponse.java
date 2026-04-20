package UnitSystem.demo.DataAccessLayer.Dto.Role;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;
    private List<PermissionResponse> permissions;
}
