package UnitSystem.demo.DataAccessLayer.Dto.Auth;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /** Set for registration/login responses so audit logging can associate the acting user before/during JWT context. */
    private Long userId;
    private String Username;
    private  String Token;
    private Set<String> userPermissions;
}
