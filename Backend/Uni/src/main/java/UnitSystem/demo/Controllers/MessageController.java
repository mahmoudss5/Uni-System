package UnitSystem.demo.Controllers;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.MessageService;
import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Message.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Endpoints for course chat message management")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Send a message in a course chat")
    @PostMapping
    public ResponseEntity<Void> createMessage(@Valid @RequestBody MessageRequest messageRequest) {
        messageService.CreateMessage(messageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get all messages for a course (ordered by time asc)")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByCourseId(@PathVariable Long courseId) {
        List<MessageResponse> messages = messageService.GetMessagesByCourseId(courseId);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Get all messages sent by a user")
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageResponse>> getMessagesBySenderId(@PathVariable Long senderId) {
        List<MessageResponse> messages = messageService.GetMessagesBySenderId(senderId);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Count total messages in a course")
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<Long> countMessagesByCourseId(@PathVariable Long courseId) {
        long count = messageService.CountMessagesByCourseId(courseId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Delete a message by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessageById(@PathVariable Long id) {
        messageService.DeleteMessageById(id);
        return ResponseEntity.noContent().build();
    }
}
