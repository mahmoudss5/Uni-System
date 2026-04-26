package UnitSystem.demo.Security.Aspects;

import UnitSystem.demo.Security.Annotations.RequiresPermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class PermissionAspect {

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

        // 4. Permission check logic
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));

        if (!hasPermission) {
            throw new AccessDeniedException("Access Denied: Missing required permission - " + requiredPermission);
        }
    }
}
