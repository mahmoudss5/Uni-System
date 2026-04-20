package UnitSystem.demo.DataAccessLayer.Entities;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserPermissionId  implements Serializable {
    private Long userId;
    private Long permissionId;


}
