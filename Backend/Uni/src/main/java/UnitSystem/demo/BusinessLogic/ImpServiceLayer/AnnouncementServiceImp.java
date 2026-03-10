package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.Aspect.Security.TeachersOnly;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AnnouncementService;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Announcement;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Repositories.AnnouncementRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImp implements AnnouncementService {

        private final AnnouncementRepository announcementRepository;
        private final CourseRepository courseRepository;

        private AnnouncementResponse mapToResponse(Announcement announcement) {
                return AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getDescription())
                                .courseId(announcement.getCourse().getId())
                                .createdDate(java.time.LocalDateTime.now())
                                .build();
        }

        @Override
        @TeachersOnly
        @CacheEvict(value = "announcementsCache", allEntries = true)
        public void createAnnouncement(AnnouncementRequest request) {
                Course course = courseRepository.findById(request.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found"));

                Announcement announcement = Announcement.builder()
                                .course(course)
                                .title(request.getTitle())
                                .description(request.getContent())
                                .build();

                announcementRepository.save(announcement);

        }

        @Override
        @TeachersOnly
        @CacheEvict(value = "announcementsCache", allEntries = true)
        public void deleteAnnouncement(Long id) {
                Announcement announcement = announcementRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Announcement not found"));
                announcementRepository.delete(announcement);
        }

        @Override
        @TeachersOnly
        @Cacheable(value = "announcementsCache", key = "'announcementById:' + #id")
        public AnnouncementResponse getAnnouncementById(Long id) {
                Announcement announcement = announcementRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Announcement not found"));
                return mapToResponse(announcement);
        }

        @Override
        @TeachersOnly
        @Cacheable(value = "announcementsCache", key = "'announcementsByCourse:' + #courseId")
        public List<AnnouncementResponse> getAnnouncementsByCourseId(Long courseId) {
                List<Announcement> announcements = announcementRepository.findByCourseId(courseId);
                return announcements.stream()
                                .map(this::mapToResponse)
                                .collect(toList());
        }

        @Override
        @Cacheable(value = "announcementsCache", key = "'allAnnouncements'")
        public List<AnnouncementResponse> getAllAnnouncements() {

                List<Announcement> announcements = announcementRepository.findAll();
                return announcements.stream()
                                .map(this::mapToResponse)
                                .collect(toList());
        }
}
