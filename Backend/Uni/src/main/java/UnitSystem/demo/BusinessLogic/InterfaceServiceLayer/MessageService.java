package UnitSystem.demo.BusinessLogic.InterfaceServiceLayer;

import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageResponse;

import java.util.List;

public interface MessageService {

    void CreateMessage(MessageRequest messageRequest);
    List<MessageResponse> GetMessagesByCourseId(Long courseId);
    List<MessageResponse> GetMessagesBySenderId(Long senderId);
    long CountMessagesByCourseId(Long courseId);
    void DeleteMessageById(Long messageId);
}
