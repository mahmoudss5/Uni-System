package UnitSystem.demo.Security.Aspects;

import UnitSystem.demo.DataAccessLayer.Entities.Permission;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Entities.UserPermission;
import UnitSystem.demo.DataAccessLayer.Repositories.PermissionRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserPermissions;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import UnitSystem.demo.Security.Annotations.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionRepository permissionRepository;
    private final UserPermissions  userPermissionsRepository;
    private final UserRepository userRepository;
    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        String requiredPermission = requiresPermission.value();

        // 1. Try to get authentication from the standard SecurityContext (Works for REST)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. If null, we might be in a WebSocket thread. Let's check the method arguments.
        if (authentication == null) {
            // English comment: Loop through method arguments to find SimpMessageHeaderAccessor
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof org.springframework.messaging.simp.SimpMessageHeaderAccessor accessor) {
                    // English comment: Extract the user we previously set in the WebSocketAuthInterceptor
                    java.security.Principal principal = accessor.getUser();
                    if (principal instanceof Authentication auth) {
                        authentication = auth;
                    }
                    break;
                }
            }
        }

        // 3. Final authentication check
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }



         String permissionName= requiresPermission.value();
         Permission permission=permissionRepository.findByName(permissionName)
                 .orElseThrow(() -> new AccessDeniedException("Required permission not found: " + permissionName));


         String userEmail = currentUser();
        User user=userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + userEmail));

         Optional<UserPermission> userPermissionOpt = userPermissionsRepository.findByUser_IdAndPermission_Id(user.getId(), permission.getId());

       boolean hasPermission=true;
       if(userPermissionOpt.isPresent() && !userPermissionOpt.get().isGranted()) {
           hasPermission=false;
       }

        // 4. Permission check logic
        /*Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));*/

        if (!hasPermission) {
            throw new AccessDeniedException("Access Denied: Missing required permission - " + requiredPermission);
        }
    }

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
