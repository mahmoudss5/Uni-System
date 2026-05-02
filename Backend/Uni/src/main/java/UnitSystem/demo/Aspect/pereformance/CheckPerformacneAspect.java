package UnitSystem.demo.Aspect.pereformance;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AuditLogService;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogRequest;
import UnitSystem.demo.Security.Util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckPerformacneAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(UnitSystem.demo.Aspect.pereformance.PerformanceMonitor)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            String ip= SecurityUtils.getInstance().getUserIp();
            long duration = System.currentTimeMillis() - startTime;
            String methodName = joinPoint.getSignature().toShortString();
            if(duration > 500) {
                AuditLogRequest logRequest =AuditLogRequest.builder()
                        .action("Performance Alert: " + methodName)
                        .ipAddress(ip)
                        .details("Execution time: " + duration + " ms")
                        .build();
                auditLogService.saveAsyincAuditLog(logRequest);
                log.warn("Performance Alert: " + methodName + " - " + duration + " ms");
            }else if(duration <= 500) {
                log.info("Performance: " + methodName + " - " + duration + " ms");
            }
        }
    }

}
