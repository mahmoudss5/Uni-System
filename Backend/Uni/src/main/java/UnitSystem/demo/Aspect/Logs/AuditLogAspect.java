package UnitSystem.demo.Aspect.Logs;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AuditLogService;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Auth.AuthResponse;
import UnitSystem.demo.Security.Util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import static UnitSystem.demo.Security.Util.SecurityUtils.getCurrentUserId;
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(UnitSystem.demo.Aspect.Logs.AuditLog)", returning = "result")
    public void logAfterMethodExecution(JoinPoint joinPoint, Object result) {
        log.info("Method executed successfully with result: {}", result);

        String methodName = joinPoint.getSignature().getName();
        String details = "Executed method: " + methodName + " with result: " + result;

        Long userId = getCurrentUserId();
        // Registration / other pre-auth flows: SecurityContext often has no SecurityUser yet.
        if (userId == null && result instanceof AuthResponse authResponse && authResponse.getUserId() != null) {
            userId = authResponse.getUserId();
        }

        AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                .userId(userId)
                .action(methodName)
                .details(details)
                .ipAddress(SecurityUtils.getInstance().getUserIp()) 
                .build();
        auditLogService.createAuditLog(auditLogRequest);
    }
}
