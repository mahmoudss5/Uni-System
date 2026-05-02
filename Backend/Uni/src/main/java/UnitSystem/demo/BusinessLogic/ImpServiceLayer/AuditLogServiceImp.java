package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AuditLogService;
import UnitSystem.demo.BusinessLogic.Mappers.AuditLogMapper;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogRequest;
import UnitSystem.demo.DataAccessLayer.Dto.AuditLog.AuditLogResponse;
import UnitSystem.demo.DataAccessLayer.Entities.AuditLog;
import UnitSystem.demo.DataAccessLayer.Entities.Values.RoleType;
import UnitSystem.demo.DataAccessLayer.Repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImp implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getAuditLogsByUserName(String userName) {
        return auditLogRepository.findAllByUserUserName(userName).stream()
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getAuditLogsByAction(String action) {
        return auditLogRepository.findAllByAction(action).stream()
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getAuditLogsByActionAndUserName(String action, String userName) {
        return auditLogRepository.findAllByActionAndUserUserName(action, userName).stream()
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public AuditLogResponse getAuditLogById(Long auditLogId) {
        return auditLogRepository.findById(auditLogId)
                .map(auditLogMapper::mapToAuditLogResponse)
                .orElse(null);
    }

    @Override
    @CacheEvict(value = "auditLogsCache", allEntries = true)
    public AuditLogResponse createAuditLog(AuditLogRequest auditLogRequest) {
        AuditLog auditLog = auditLogMapper.mapToAuditLog(auditLogRequest);
        auditLogRepository.save(auditLog);
        return auditLogMapper.mapToAuditLogResponse(auditLog);
    }

    @Override
    @CacheEvict(value = "auditLogsCache", allEntries = true)
    public void deleteAuditLog(Long auditLogId) {
        auditLogRepository.deleteById(auditLogId);
    }

    @Override
    public List<AuditLogResponse> getLastWeekStudentsLogs() {
        return auditLogRepository.findAllByUserRolesName(RoleType.Student.name()).stream()
                .filter(auditLog -> auditLog.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLastWeekTeachersLogs() {
        return auditLogRepository.findAllByUserRolesName(RoleType.Teacher.name()).stream()
                .filter(auditLog -> auditLog.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLastWeekAdminsLogs() {
        return auditLogRepository.findAllByUserRolesName(RoleType.Admin.name()).stream()
                .filter(auditLog -> auditLog.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .map(auditLogMapper::mapToAuditLogResponse)
                .toList();
    }

    @Override
    @Async("auditLogExecutor")
    public void saveAsyincAuditLog(AuditLogRequest auditLogRequest) {
         AuditLog auditLog = auditLogMapper.mapToAuditLog(auditLogRequest);
         auditLogRepository.save(auditLog);
    }
}
