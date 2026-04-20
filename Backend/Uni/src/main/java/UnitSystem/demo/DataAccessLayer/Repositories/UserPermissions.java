package UnitSystem.demo.DataAccessLayer.Repositories;

import UnitSystem.demo.DataAccessLayer.Entities.UserPermission;
import UnitSystem.demo.DataAccessLayer.Entities.UserPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserPermissions extends JpaRepository<UserPermission, UserPermissionId> {
    List<UserPermission> findByUser_Id(Long userId);

    List<UserPermission> findAllByUser_IdIn(Collection<Long> userIds);

    Optional<UserPermission> findByUser_IdAndPermission_Id(Long userId, Long permissionId);

    Optional<UserPermission> findByUser_IdAndPermission_Name(Long userId, String permissionName);
}
