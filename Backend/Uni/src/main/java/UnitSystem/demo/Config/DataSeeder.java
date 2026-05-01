package UnitSystem.demo.Config;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.DepartmentService;
import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.UserService;
import UnitSystem.demo.DataAccessLayer.Dto.User.UserRequest;
import UnitSystem.demo.DataAccessLayer.Entities.DepartmentsType;
import UnitSystem.demo.DataAccessLayer.Entities.Permission;
import UnitSystem.demo.DataAccessLayer.Entities.Role;
import UnitSystem.demo.DataAccessLayer.Entities.RoleType;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.PermissionRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.RoleRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.StudentRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.TeacherRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
public class DataSeeder {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "test1234";

    private final UserService userService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            Arrays.stream(RoleType.values()).forEach(roleType -> {
                if (!roleRepository.existsByName(roleType.name())) {
                    roleRepository.save(Role.builder().name(roleType.name()).build());
                    System.out.println("Seeded Role: " + roleType.name());
                }
            });

            Arrays.stream(DepartmentsType.values()).forEach(departmentType -> {
                if (departmentService.getDepartmentByName(departmentType.name()) == null) {
                    departmentService.createDepartment(
                            UnitSystem.demo.DataAccessLayer.Dto.Department.DepartmentRequest.builder()
                                    .name(departmentType.name())
                                    .build());
                    System.out.println("Seeded Department: " + departmentType.name());
                }
            });

            Optional<User> adminOptional = userService.findByEmail(DEFAULT_ADMIN_EMAIL);
            if (adminOptional.isEmpty()) {
                UserRequest adminRequest = UserRequest.builder()
                        .username(DEFAULT_ADMIN_USERNAME)
                        .email(DEFAULT_ADMIN_EMAIL)
                        .password(DEFAULT_ADMIN_PASSWORD)
                        .build();
                userService.save(adminRequest);
                System.out.println("Seeded Admin User: " + DEFAULT_ADMIN_EMAIL);
                adminOptional = userService.findByEmail(DEFAULT_ADMIN_EMAIL);
            }

            adminOptional.ifPresent(adminUser -> {
                Role adminRole = roleRepository.findByName(RoleType.Admin.name())
                        .orElseThrow(() -> new IllegalStateException("Admin role must exist before seeding users"));

                adminUser.setUserName(DEFAULT_ADMIN_USERNAME);
                adminUser.setActive(true);
                adminUser.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));

                if (adminUser.getRoles() == null) {
                    adminUser.setRoles(new HashSet<>());
                }
                if (adminUser.getRoles().stream().noneMatch(role -> adminRole.getId().equals(role.getId()))) {
                    adminUser.getRoles().add(adminRole);
                }

                userService.save(adminUser);
                System.out.println("Admin user: " + adminUser.getEmail());
                System.out.println("Admin password: " + DEFAULT_ADMIN_PASSWORD);
                System.out.println("Ensured Admin role for: " + adminUser.getEmail());
            });

       /*     userRepository.findAll().forEach(user -> {
                Long userId = user.getId();
                if (userId == null) {
                    return;
                }
                if (studentRepository.existsById(userId)) {
                    userService.assignRoleToUser(userId, RoleType.Student);
                    System.out.println("inster !!!!!!!!   Student Role for: " + userId);
                }
                if (teacherRepository.existsById(userId)) {
                    userService.assignRoleToUser(userId, RoleType.Teacher);
                    System.out.println("inster !!!!!!!!   Teacher Role for: " + userId);
                }
            });*/
        };
    }
}