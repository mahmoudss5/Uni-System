package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.CourseService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.MessageService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService;
import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Message;
import UnitSystem.demo.DataAccessLayer.Repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImp implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final CourseService courseService;

    private Message MapToMessageEntity(MessageRequest messageRequest) {
        var user = userService.findUserById(messageRequest.getSenderId());
        var course = courseService.getCourseEntityById(messageRequest.getCourseId());

        return Message.builder()
                .content(messageRequest.getContent())
                .sender(user)
                .course(course)
                .build();
    }

    private MessageResponse MapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .courseId(message.getCourse().getId())
                .courseName(message.getCourse().getName())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUserName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    @Override
    @CacheEvict(value = "messagesCache", allEntries = true)
    public void CreateMessage(MessageRequest messageRequest) {
        log.info("Creating message for course ID: {}", messageRequest.getCourseId());
        Message message = MapToMessageEntity(messageRequest);
        messageRepository.save(message);
    }

    @Override
    @Cacheable(value = "messagesCache", key = "'messagesByCourse:' + #courseId")
    public List<MessageResponse> GetMessagesByCourseId(Long courseId) {
        log.info("Fetching messages for course ID: {}", courseId);
        return messageRepository.findByCourseIdOrderByCreatedAtAsc(courseId).stream()
                .map(this::MapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "messagesCache", key = "'messagesBySender:' + #senderId")
    public List<MessageResponse> GetMessagesBySenderId(Long senderId) {
        log.info("Fetching messages for sender ID: {}", senderId);
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(senderId).stream()
                .map(this::MapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "messagesCache", key = "'countByCourse:' + #courseId")
    public long CountMessagesByCourseId(Long courseId) {
        log.info("Counting messages for course ID: {}", courseId);
        return messageRepository.countByCourseId(courseId);
    }

    @Override
    @CacheEvict(value = "messagesCache", allEntries = true)
    public void DeleteMessageById(Long messageId) {
        log.info("Deleting message ID: {}", messageId);
        messageRepository.deleteById(messageId);
    }
}
