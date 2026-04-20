package UnitSystem.demo.BusinessLogic.Mappers;

import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Role.RoleResponse;
import UnitSystem.demo.DataAccessLayer.Dto.User.UserRequest;
import UnitSystem.demo.DataAccessLayer.Dto.User.UserResponse;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserResponse mapToUserResponse(User user) {
        return mapToUserResponse(user, List.of());
    }

    public UserResponse mapToUserResponse(User user, List<UserPermissionResponse> userPermissions) {
        var roleDtos = user.getRoles() == null
                ? new HashSet<RoleResponse>()
                : user.getRoles().stream()
                        .map(r -> RoleResponse.builder().id(r.getId()).name(r.getName()).build())
                        .collect(Collectors.toSet());
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUserName())
                .email(user.getEmail())
                .active(user.getActive())
                .roles(roleDtos)
                .userPermissions(userPermissions == null ? List.of() : userPermissions)
                .build();
    }

    public User mapToUser(UserRequest userRequest) {
        return User.builder()
                .userName(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .active(true)
                .roles(new HashSet<>())
                .build();
    }
}
