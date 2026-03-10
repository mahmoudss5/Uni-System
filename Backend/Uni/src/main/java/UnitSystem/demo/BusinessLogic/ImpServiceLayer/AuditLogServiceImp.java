package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AuditLogService;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogRequest;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogResponse;
import UnitSystem.demo.DataAccessLayer.Entities.AuditLog;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.AuditLogRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImp implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    private AuditLogResponse mapToAuditLogResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUser() != null ? auditLog.getUser().getId() : null)
                .userName(auditLog.getUser() != null ? auditLog.getUser().getUserName() : null)
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }

    private AuditLog mapToAuditLog(AuditLogRequest auditLogRequest) {
        User user = null;
        if (auditLogRequest.getUserId() != null) {
            user = userRepository.findById(auditLogRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        return AuditLog.builder()
                .user(user)
                .action(auditLogRequest.getAction())
                .details(auditLogRequest.getDetails())
                .ipAddress(auditLogRequest.getIpAddress())
                .build();
    }

    @Override
    @Cacheable(value = "auditLogsCache", key = "'allAuditLogs'")
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "auditLogsCache", key = "'auditLogsByUser:' + #userName")
    public List<AuditLogResponse> getAuditLogsByUserName(String userName) {
        return auditLogRepository.findAllByUserUserName(userName).stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "auditLogsCache", key = "'auditLogsByAction:' + #action")
    public List<AuditLogResponse> getAuditLogsByAction(String action) {
        return auditLogRepository.findAllByAction(action).stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "auditLogsCache", key = "'auditLogsByActionAndUser:' + #action + ':' + #userName")
    public List<AuditLogResponse> getAuditLogsByActionAndUserName(String action, String userName) {
        return auditLogRepository.findAllByActionAndUserUserName(action, userName).stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "auditLogsCache", key = "'auditLogById:' + #auditLogId")
    public AuditLogResponse getAuditLogById(Long auditLogId) {
        return auditLogRepository.findById(auditLogId)
                .map(this::mapToAuditLogResponse)
                .orElse(null);
    }

    @Override
    @CacheEvict(value = "auditLogsCache", allEntries = true)
    public AuditLogResponse createAuditLog(AuditLogRequest auditLogRequest) {
        AuditLog auditLog = mapToAuditLog(auditLogRequest);
        auditLogRepository.save(auditLog);
        return mapToAuditLogResponse(auditLog);
    }

    @Override
    @CacheEvict(value = "auditLogsCache", allEntries = true)
    public void deleteAuditLog(Long auditLogId) {
        auditLogRepository.deleteById(auditLogId);
    }
}
