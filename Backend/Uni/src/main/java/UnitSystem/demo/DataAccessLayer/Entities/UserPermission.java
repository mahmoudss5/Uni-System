package UnitSystem.demo.DataAccessLayer.Entities;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "user_permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermission {
    @EmbeddedId
    private UserPermissionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Column(nullable = false)
    private boolean granted = true;


}
