package UnitSystem.demo.DataAccessLayer.Dto.Auth;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String Username;
    private  String Token;
    private Set<String> userPermissions;
}
