package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.NotificationService;
import UnitSystem.demo.DataAccessLayer.Dto.Notification.Course.NotificationCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Notification.User.NotificationRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Notification.User.NotificationResponse;
import UnitSystem.demo.DataAccessLayer.Entities.*;
import UnitSystem.demo.DataAccessLayer.Repositories.NotificationRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CourseRepository courseRepository;
    // ──────────────────────────────────────────────────────────────
    // Mappers
    // ──────────────────────────────────────────────────────────────

    private Notification mapToNotificationEntity(NotificationRequest request) {
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getRecipientId()));

        return Notification.builder()
                .recipient(recipient)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType() != null ? request.getType() : NotificationType.SYSTEM)
                .build();
    }

    private Notification buildNotificationForUser(User recipient, String title, String message, NotificationType type) {
        return Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .build();
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipient().getId())
                .recipientName(notification.getRecipient().getUserName())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Write Operations — evict cache
    // ──────────────────────────────────────────────────────────────

    @Override
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {
        log.info("Creating notification for recipient ID: {}", notificationRequest.getRecipientId());
        Notification notification = mapToNotificationEntity(notificationRequest);
        notificationRepository.save(notification);
        return mapToNotificationResponse(notification);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public NotificationResponse markAsRead(Long notificationId) {
        log.info("Marking notification ID: {} as read", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
        return mapToNotificationResponse(notification);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public int markAllAsReadForUser(Long userId) {
        log.info("Marking all notifications as read for user ID: {}", userId);
        return notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public void deleteNotificationById(Long notificationId) {
        log.info("Deleting notification ID: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public void deleteAllNotificationsForUser(Long userId) {
        log.info("Deleting all notifications for user ID: {}", userId);
        notificationRepository.deleteAllByRecipientId(userId);
    }

    @Override
    public void sendNotificationToUser(NotificationRequest notificationRequest) {
        log.info("Sending notification to user ID: {}", notificationRequest.getRecipientId());
        Notification notification = mapToNotificationEntity(notificationRequest);
        notificationRepository.save(notification);
        NotificationResponse notificationResponse = mapToNotificationResponse(notification);
        simpMessagingTemplate.convertAndSendToUser(
                notification.getRecipient().getEmail(),
                "/queue/notifications",
                notificationResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationsCache", allEntries = true)
    public void sendNotificationToCourse(NotificationCourseRequest notificationRequest) {
        log.info("Sending notification to course ID: {}", notificationRequest.getCourseId());
        var course = courseRepository.findById(notificationRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + notificationRequest.getCourseId()));
        Set<EnrolledCourse> enrolledCourses = course.getCourseEnrollments();
        if (enrolledCourses.isEmpty()) {
            log.warn("No enrolled students for course ID: {}", notificationRequest.getCourseId());
            return;
        }
        NotificationType type = notificationRequest.getType() != null
                ? notificationRequest.getType()
                : NotificationType.ANNOUNCEMENT;
        List<Notification> notifications = enrolledCourses.stream()
                .map(enrollment -> buildNotificationForUser(
                        enrollment.getStudent(),
                        notificationRequest.getTitle(),
                        notificationRequest.getMessage(),
                        type))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);
        notifications.forEach(notification ->
                simpMessagingTemplate.convertAndSendToUser(
                        notification.getRecipient().getEmail(),
                        "/queue/notifications",
                        mapToNotificationResponse(notification)));
    }

    // ──────────────────────────────────────────────────────────────
    // Read Operations — cached
    // ──────────────────────────────────────────────────────────────

    @Override
    @Cacheable(value = "notificationsCache", key = "'notificationById:' + #notificationId")
    public NotificationResponse getNotificationById(Long notificationId) {
        log.info("Fetching notification ID: {}", notificationId);
        return notificationRepository.findById(notificationId)
                .map(this::mapToNotificationResponse)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
    }

    @Override
    @Cacheable(value = "notificationsCache", key = "'allForUser:' + #userId")
    public List<NotificationResponse> getAllNotificationsForUser(Long userId) {
        log.info("Fetching all notifications for user ID: {}", userId);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "notificationsCache", key = "'unreadForUser:' + #userId")
    public List<NotificationResponse> getUnreadNotificationsForUser(Long userId) {
        log.info("Fetching unread notifications for user ID: {}", userId);
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "notificationsCache", key = "'byType:' + #userId + ':' + #type")
    public List<NotificationResponse> getNotificationsByType(Long userId, NotificationType type) {
        log.info("Fetching {} notifications for user ID: {}", type, userId);
        return notificationRepository.findByRecipientIdAndTypeOrderByCreatedAtDesc(userId, type).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "notificationsCache", key = "'unreadCount:' + #userId")
    public long countUnreadForUser(Long userId) {
        log.info("Counting unread notifications for user ID: {}", userId);
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }
}
