package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.PermissionService;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Permission;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Entities.UserPermission;
import UnitSystem.demo.DataAccessLayer.Entities.UserPermissionId;
import UnitSystem.demo.DataAccessLayer.Repositories.PermissionRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserPermissions;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PermissionServiceImp implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserPermissions userPermissionsRepository;
    private final UserRepository userRepository;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    @Override
    public PermissionResponse getPermissionById(Long permissionId) {
        Long id = Objects.requireNonNull(permissionId, "permissionId cannot be null");
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        String permissionName = Objects.requireNonNull(request.getName(), "permission name cannot be null");

        permissionRepository.findByName(permissionName)
                .ifPresent(existing -> {
                    throw new RuntimeException("Permission name already exists");
                });

        Permission permission = Permission.builder()
                .name(permissionName)
                .description(request.getDescription())
                .build();

        Permission savedPermission = permissionRepository.save(Objects.requireNonNull(permission));
        return toPermissionResponse(savedPermission);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long permissionId, PermissionRequest request) {
        Long id = Objects.requireNonNull(permissionId, "permissionId cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        String permissionName = Objects.requireNonNull(request.getName(), "permission name cannot be null");

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permissionRepository.findByName(permissionName)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("Permission name already exists");
                    }
                });

        permission.setName(permissionName);
        permission.setDescription(request.getDescription());
        Permission updatedPermission = permissionRepository.save(permission);
        return toPermissionResponse(updatedPermission);
    }

    @Override
    @Transactional
    public void deletePermission(Long permissionId) {
        Long id = Objects.requireNonNull(permissionId, "permissionId cannot be null");
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserPermissionResponse assignPermissionToUser(UserPermissionRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        Long userId = Objects.requireNonNull(request.getUserId(), "userId cannot be null");
        Long permissionId = Objects.requireNonNull(request.getPermissionId(), "permissionId cannot be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        UserPermission userPermission = userPermissionsRepository
                .findByUser_IdAndPermission_Id(userId, permissionId)
                .orElseGet(() -> {
                    UserPermission created = new UserPermission();
                    created.setId(new UserPermissionId(request.getUserId(),request.getPermissionId()));
                    created.getId().setUserId(userId);
                    created.getId().setPermissionId(permissionId);
                    created.setUser(user);
                    created.setPermission(permission);
                    return created;
                });

        boolean granted = request.getGranted() == null || request.getGranted();
        userPermission.setGranted(granted);

        UserPermission saved = userPermissionsRepository.save(userPermission);
        return toUserPermissionResponse(saved);
    }

    @Override
    @Transactional
    public void removePermissionFromUser(Long userId, Long permissionId) {
        Permission permission=permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserPermission> userPermissions=userPermissionsRepository.findByUser_Id(userId);
        Boolean found=false;
        for (UserPermission userPermission:userPermissions) {
            if (userPermission.getPermission().getId().equals(permission.getId())) {
                 userPermission.setGranted(false);
                 userPermissionsRepository.save(userPermission);
                 found=true;
                 break;
            }
        }
        if (!found) {
           UserPermission userPermission=UserPermission.builder()
                   .id(new UserPermissionId(userId, permissionId))
                   .user(user)
                   .permission(permission)
                   .granted(false)
                   .build();
           userPermissionsRepository.save(userPermission);
        }
        return;
    }

    @Override
    public List<UserPermissionResponse> getUserPermissions(Long userId) {
        Long id = Objects.requireNonNull(userId, "userId cannot be null");
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        return userPermissionsRepository.findByUser_Id(id).stream()
                .map(this::toUserPermissionResponse)
                .toList();
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }

    private UserPermissionResponse toUserPermissionResponse(UserPermission userPermission) {
        return UserPermissionResponse.builder()
                .userId(userPermission.getUser().getId())
                .permissionId(userPermission.getPermission().getId())
                .permissionName(userPermission.getPermission().getName())
                .granted(userPermission.isGranted())
                .build();
    }
}
