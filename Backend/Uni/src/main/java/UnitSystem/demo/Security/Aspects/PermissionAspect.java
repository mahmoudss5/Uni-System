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
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));
                
        if (!hasPermission) {
            throw new AccessDeniedException("Access Denied: Missing required permission - " + requiredPermission);
        }
    }
}
