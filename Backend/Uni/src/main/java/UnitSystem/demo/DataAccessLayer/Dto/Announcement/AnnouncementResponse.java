package UnitSystem.demo.DataAccessLayer.Dto.Announcement;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnnouncementResponse {

    private Long id;
    private String title;
    private String content;
    private Long courseId;
    private LocalDateTime createdDate;
}
