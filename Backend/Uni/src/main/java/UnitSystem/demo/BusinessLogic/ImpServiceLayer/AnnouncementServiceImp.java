package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Logs.AuditLog;
import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.*;
import UnitSystem.demo.BusinessLogic.Mappers.AnnouncementMapper;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Notification.Course.NotificationCourseRequest;
import UnitSystem.demo.DataAccessLayer.Dto.EnrolledCourse.EnrolledCourseResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Student.StudentResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Announcement;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.NotificationType;
import UnitSystem.demo.DataAccessLayer.Entities.Student;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import UnitSystem.demo.DataAccessLayer.Repositories.AnnouncementRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImp implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrolledCourseService enrolledCourseService;
    private final TeacherService teacherService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AnnouncementMapper announcementMapper;
    private final NotificationService notificationService;

    @Override
    @TeachersOnly
    @CacheEvict(value = "announcementsCache", allEntries = true)
    @AuditLog
    public void createAnnouncement(AnnouncementRequest request) {
        Course course = courseService.getCourseEntityById(request.getCourseId());
        Announcement announcement = Announcement.builder()
                .course(course)
                .title(request.getTitle())
                .description(request.getContent())
                .build();
        announcementRepository.save(announcement);
        sendAnnouncementToCourseUsers(announcement);
        sendAnnouncementNotificationToCourseStudents(announcement);
        log.info("Created announcement successfully: {}", announcement);
    }

    @Override
    @TeachersOnly
    @CacheEvict(value = "announcementsCache", allEntries = true)
    @AuditLog
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcementRepository.delete(announcement);
    }

    @Override
    @Cacheable(value = "announcementsCache", key = "'announcementById:' + #id")
    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        return announcementMapper.mapToResponse(announcement);
    }

    @Override
    @Cacheable(value = "announcementsCache", key = "'announcementsByCourse:' + #courseId")
    public List<AnnouncementResponse> getAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findByCourseId(courseId);
        return announcements.stream()
                .map(announcementMapper::mapToResponse)
                .collect(toList());
    }

    public void sendAnnouncementToCourseUsers(Announcement announcement) {
        Long courseId = announcement.getCourse().getId();
        String destination = "/queue/announcements";
        List<String> emails = courseService.findStudentEmailsByCourseId(courseId);
        for (String email : emails) {
            simpMessagingTemplate.convertAndSendToUser(email, destination,
                    announcementMapper.mapToResponse(announcement));
        }
    }

    private void sendAnnouncementNotificationToCourseStudents(Announcement announcement) {
        NotificationCourseRequest notificationRequest = NotificationCourseRequest.builder()
                .courseId(announcement.getCourse().getId())
                .title("New Announcement: " + announcement.getTitle())
                .message(announcement.getDescription())
                .type(NotificationType.ANNOUNCEMENT)
                .build();
        notificationService.sendNotificationToCourse(notificationRequest);
    }

    @Override
    @Cacheable(value = "announcementsCache", key = "'allAnnouncements'")
    public List<AnnouncementResponse> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAll();
        return announcements.stream()
                .map(announcementMapper::mapToResponse)
                .collect(toList());
    }

    @Override
    @Cacheable(value = "announcementsCache", key = "'announcementsByStudent:' + #studentId")
    public List<AnnouncementResponse> getAllAnnouncementsByStudentId(Long studentId) {
        Student student = studentService.getStudentEntityById(studentId);
       return student.getEnrolledCourses().stream()
                .flatMap(enrolledCourse -> announcementRepository.findByCourseId(enrolledCourse.getCourse().getId()).stream())
                .map(announcementMapper::mapToResponse)
               .sorted(Comparator.comparing(AnnouncementResponse::getCreatedDate).reversed())
                .limit(5)
                .toList();
    }

    @Override
    @Cacheable(value = "announcementsCache", key = "'announcementsByTeacher:' + #teacherId")
    public List<AnnouncementResponse> getAllAnnouncementsByTeacherId(Long teacherId) {
       Teacher teacher = teacherService.findTeacherEntityById(teacherId);
       return teacher.getCourses()
               .stream()
               .flatMap(course -> announcementRepository.findByCourseId(course.getId()).stream())
               .map(announcementMapper::mapToResponse)
               .sorted(Comparator.comparing(AnnouncementResponse::getCreatedDate).reversed())
               .limit(5)
               .toList();

    }

}
