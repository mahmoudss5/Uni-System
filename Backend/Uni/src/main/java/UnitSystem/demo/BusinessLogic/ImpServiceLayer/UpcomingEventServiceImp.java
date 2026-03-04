package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UpcomingEventService;
import UnitSystem.demo.DataAccessLayer.Dto.UpcomingEvent.UpcomingEventRequest;
import UnitSystem.demo.DataAccessLayer.Dto.UpcomingEvent.UpcomingEventResponse;
import UnitSystem.demo.DataAccessLayer.Entities.EventType;
import UnitSystem.demo.DataAccessLayer.Entities.UpcomingEvent;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.UpcomingEventRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UpcomingEventServiceImp implements UpcomingEventService {

    private final UpcomingEventRepository upcomingEventRepository;
    private final UserRepository userRepository;

    private UpcomingEventResponse mapToResponse(UpcomingEvent event) {
        return UpcomingEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .subtitle(event.getSubtitle())
                .eventDate(event.getEventDate())
                .type(event.getType().name())
                .userId(event.getUser().getId())
                .userName(event.getUser().getUserName())
                .createdAt(event.getCreatedAt())
                .build();
    }

    private UpcomingEvent mapToEntity(UpcomingEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        return UpcomingEvent.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .eventDate(request.getEventDate())
                .type(EventType.valueOf(request.getType()))
                .user(user)
                .build();
    }

    @Override
    public List<UpcomingEventResponse> getAllEvents() {
        return upcomingEventRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UpcomingEventResponse> getUpcomingEvents() {
        return upcomingEventRepository.findUpcomingFromDate(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UpcomingEventResponse> getEventsByType(String type) {
        EventType eventType = EventType.valueOf(type.toUpperCase());
        return upcomingEventRepository.findByType(eventType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UpcomingEventResponse> getEventsByUser(Long userId) {
        return upcomingEventRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UpcomingEventResponse getEventById(Long id) {
        return upcomingEventRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Upcoming event not found with ID: " + id));
    }

    @Override
    public UpcomingEventResponse createEvent(UpcomingEventRequest request) {
        UpcomingEvent event = mapToEntity(request);
        upcomingEventRepository.save(event);
        return mapToResponse(event);
    }

    @Override
    public UpcomingEventResponse updateEvent(Long id, UpcomingEventRequest request) {
        UpcomingEvent existing = upcomingEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Upcoming event not found with ID: " + id));

        existing.setTitle(request.getTitle());
        existing.setSubtitle(request.getSubtitle());
        existing.setEventDate(request.getEventDate());
        existing.setType(EventType.valueOf(request.getType()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
        existing.setUser(user);

        upcomingEventRepository.save(existing);
        return mapToResponse(existing);
    }

    @Override
    public void deleteEvent(Long id) {
        upcomingEventRepository.deleteById(id);
    }
}
