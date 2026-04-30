package UnitSystem.demo.Security.Audit;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AuditLogService;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogRequest;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditLogger {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public void logUnauthorizedAccess(HttpServletRequest request, Authentication authentication, String reason, int statusCode) {
        try {
            Long userId = resolveUserId(authentication);
            String action = statusCode == 401 ? "UNAUTHORIZED_ACCESS_ATTEMPT" : "ACCESS_DENIED";
            String details = String.format(
                    "Blocked request %s %s. reason=%s",
                    request.getMethod(),
                    request.getRequestURI(),
                    reason == null ? "N/A" : reason
            );

            AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                    .userId(userId)
                    .action(action)
                    .details(details)
                    .ipAddress(resolveClientIp(request))
                    .build();

            auditLogService.createAuditLog(auditLogRequest);
        } catch (Exception exception) {
            log.warn("Failed to write security audit log for blocked request", exception);
        }
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).map(user -> user.getId()).orElse(null);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
