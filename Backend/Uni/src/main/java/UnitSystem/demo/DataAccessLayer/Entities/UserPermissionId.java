package UnitSystem.demo.DataAccessLayer.Entities;


import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserPermissionId  implements Serializable {
    private Long userId;
    private Long permissionId;
    public UserPermissionId(Long userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }
}
