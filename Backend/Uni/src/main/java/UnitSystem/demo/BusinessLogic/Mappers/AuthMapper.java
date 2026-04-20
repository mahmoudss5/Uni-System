package UnitSystem.demo.BusinessLogic.Mappers;

import UnitSystem.demo.DataAccessLayer.Dto.Auth.AuthResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Role;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.UserPermissions;
import UnitSystem.demo.Security.Jwt.JwtService;
import UnitSystem.demo.Security.User.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthMapper {

    private final JwtService jwtService;
    private final UserPermissions userPermissionsRepository;

    public AuthResponse mapToAuthResponse(User user) {
        log.info("Mapping User to AuthResponse: {}", user);
        Set<String> effectivePermissions = new HashSet<>();

        for (Role role : user.getRoles()) {
            role.getPermissions().forEach(permission -> effectivePermissions.add(permission.getName()));
        }

        userPermissionsRepository.findByUser_Id(user.getId()).forEach(userPermission -> {
            String permissionName = userPermission.getPermission().getName();
            if (userPermission.isGranted()) {
                effectivePermissions.add(permissionName);
            } else {
                effectivePermissions.remove(permissionName);
            }
        });

        return AuthResponse.builder()
                .Username(user.getUserName())
                .Token(jwtService.generateToken(new SecurityUser(user)))
                .userPermissions(effectivePermissions)
                .build();
    }
}
