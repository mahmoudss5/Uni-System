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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
    @CacheEvict(value = "usersCache", allEntries = true)
    public UserPermissionResponse assignPermissionToUser(UserPermissionRequest request) {
        Long userId = request.getUserId();
        Long permissionId = request.getPermissionId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        UserPermission userPermission = userPermissionsRepository
                .findByUser_IdAndPermission_Id(userId, permissionId)
                .orElseGet(() -> UserPermission.builder()
                        .id(new UserPermissionId(userId, permissionId))
                        .user(user)
                        .permission(permission)
                        .build());

        boolean granted = request.getGranted() == null || request.getGranted();
        userPermission.setGranted(granted);

        UserPermission saved = userPermissionsRepository.save(userPermission);
        return toUserPermissionResponse(saved);
    }



    @Override
    @Transactional
    @CacheEvict(value = "usersCache", allEntries = true)
    public void revokePermissionFromUser(Long userId, Long permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        UserPermission userPermission = userPermissionsRepository
                .findByUser_IdAndPermission_Id(userId, permissionId)
                .orElseGet(() -> UserPermission.builder()
                        .id(new UserPermissionId(userId, permissionId))
                        .user(user)
                        .permission(permission)
                        .build());

        userPermission.setGranted(false);
        userPermissionsRepository.save(userPermission);
    }

    @Override
    @Transactional
    @CacheEvict(value = "usersCache", allEntries = true)
    public void resetUserPermissionOverride(Long userId, Long permissionId) {
        log.info("Resetting permission override for userId={} and permissionId={}", userId, permissionId);
        userPermissionsRepository.findByUser_IdAndPermission_Id(userId, permissionId)
                .ifPresent(userPermissionsRepository::delete);
    }

    @Override
    @Transactional
    @CacheEvict(value = "usersCache", allEntries = true)
    public void preventUserFromAccessingPermission(Long userId, Long permissionId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Permission permission=permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        UserPermissionId userPermissionId = new UserPermissionId(userId, permissionId);
        log.info("preventUserFromAccessingPermission UserPermissionId={} PermissionId={}", userPermissionId, permissionId);
        UserPermission userPermission=UserPermission.builder()
                .id(userPermissionId)
                .user(user)
                .permission(permission)
                .granted(false)
                .build();
        userPermissionsRepository.save(userPermission);
       log.info("UserPermission saved with id={} for userId={} and permissionId={}", userPermission.getId(), userId, permissionId);
        return ;
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

    @Override
    public Map<Long, List<UserPermissionResponse>> getUserPermissionsForUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userPermissionsRepository.findAllByUser_IdIn(userIds).stream()
                .collect(Collectors.groupingBy(
                        up -> up.getUser().getId(),
                        Collectors.mapping(this::toUserPermissionResponse, Collectors.toList())));
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
