package UnitSystem.demo.DataAccessLayer.Dto.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionRequest {
    private Long userId;
    private Long permissionId;
    private Boolean granted;
}
