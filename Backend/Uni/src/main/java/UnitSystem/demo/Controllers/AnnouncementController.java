package UnitSystem.demo.Controllers;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.AnnouncementService;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementResponse;
import UnitSystem.demo.DataAccessLayer.Repositories.AnnouncementRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Announcement Controller", description = "APIs for managing announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;


    @Operation(summary = "Create a new announcement")
    @PostMapping("/create")
    public ResponseEntity<AnnouncementResponse> post(@RequestBody AnnouncementRequest request) {
        announcementService.createAnnouncement(request);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Delete an announcement by ID")
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@RequestBody Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get an announcement by ID")
    @PostMapping("/getAllByCourseID/{id}")
    public ResponseEntity<AnnouncementResponse> getById(@RequestBody Long id) {
        AnnouncementResponse announcementResponse = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(announcementResponse);
    }

    @Operation(summary = "Get announcements by course ID")
    @PostMapping("/getByCourseId/{courseId}")
    public ResponseEntity<AnnouncementResponse> getByCourseId(@RequestBody Long courseId) {
        AnnouncementResponse announcementResponse = announcementService.getAnnouncementsByCourseId(courseId).get(0);
        return ResponseEntity.ok(announcementResponse);
    }

    @Operation(summary = "Get all announcements")
    @GetMapping("/getAll")
    public ResponseEntity<List<AnnouncementResponse>> getAll() {
        List<AnnouncementResponse> announcementResponses = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcementResponses);
    }

}
