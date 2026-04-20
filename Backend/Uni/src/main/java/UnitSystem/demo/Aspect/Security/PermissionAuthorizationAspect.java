package UnitSystem.demo.Aspect.Security;

import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.UserPermissions;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import UnitSystem.demo.ExcHandler.Entites.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static UnitSystem.demo.Security.Util.SecurityUtils.getCurrentUserId;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionAuthorizationAspect {

    private final UserRepository userRepository;
    private final UserPermissions userPermissionsRepository;

    @Before("@annotation(checkTeacherPermission)")
    public void checkTeacherPermissionBeforeOperation(CheckTeacherPermission checkTeacherPermission) {
        checkPermission(checkTeacherPermission.value().name());
    }

    @Before("@annotation(checkStudentPermission)")
    public void checkStudentPermissionBeforeOperation(CheckStudentPermission checkStudentPermission) {
        checkPermission(checkStudentPermission.value().name());
    }

    private void checkPermission(String requiredPermission) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new PermissionDeniedException("Access denied: user is not authenticated.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PermissionDeniedException("Access denied: user not found."));

        boolean hasPermission = userPermissionsRepository
                .findByUser_IdAndPermission_Name(userId, requiredPermission)
                .map(userPermission -> userPermission.isGranted())
                .orElseGet(() -> user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .anyMatch(permission -> permission.getName().equalsIgnoreCase(requiredPermission)));

        if (!hasPermission) {
            log.warn("Access denied for user {}. Missing permission: {}", user.getUserName(), requiredPermission);
            throw new PermissionDeniedException(
                    "Access denied: missing permission '" + requiredPermission + "'.");
        }
    }
}
