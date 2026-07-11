package me.xjanua.spring.backend.config;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xjanua.spring.backend.enums.UserStatus;
import me.xjanua.spring.backend.model.Permission;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.model.RolePermission;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.model.UserRole;
import me.xjanua.spring.backend.repository.PermissionRepository;
import me.xjanua.spring.backend.repository.RolePermissionRepository;
import me.xjanua.spring.backend.repository.RoleRepository;
import me.xjanua.spring.backend.repository.UserRepository;
import me.xjanua.spring.backend.repository.UserRoleRepository;

/**
 * Seeds default users (admin / user), default roles (ADMIN / USER),
 * and the permission catalogue required by the existing controllers.
 *
 * Re-runnable: existing records are looked up by natural key (email, role name,
 * permission code) and skipped if already present.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final String DEFAULT_PASSWORD = "123123";

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String USER_EMAIL = "user@gmail.com";

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    private static final List<String> ADMIN_PERMISSIONS = List.of(
            "USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
            "ROLE_READ", "ROLE_CREATE", "ROLE_UPDATE", "ROLE_DELETE",
            "PERMISSION_READ", "PERMISSION_CREATE", "PERMISSION_UPDATE", "PERMISSION_DELETE",
            "ROLE_PERMISSION_READ", "ROLE_PERMISSION_CREATE", "ROLE_PERMISSION_UPDATE",
            "ROLE_PERMISSION_DELETE",
            "CLIENT_APP_READ", "CLIENT_APP_CREATE", "CLIENT_APP_UPDATE", "CLIENT_APP_DELETE",
            "SCOPE_READ", "SCOPE_CREATE", "SCOPE_UPDATE", "SCOPE_DELETE");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Bean
    @Transactional
    public ApplicationRunner seedDatabaseRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            seedPermissions();
            seedRoles();
            seedUsers(passwordEncoder);
            assignRolePermissions();
            assignUserRoles();
            log.info("Data initialization completed.");
        };
    }

    private void seedPermissions() {
        for (String code : ADMIN_PERMISSIONS) {
            if (permissionRepository.findByCode(code).isEmpty()) {
                Permission permission = Permission.builder()
                        .code(code)
                        .description(buildPermissionDescription(code))
                        .build();
                permissionRepository.save(permission);
                log.info("Created permission: {}", code);
            }
        }
    }

    private void seedRoles() {
        createRoleIfMissing(ADMIN_ROLE);
        createRoleIfMissing(USER_ROLE);
    }

    private void createRoleIfMissing(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", name);
        }
    }

    private void seedUsers(PasswordEncoder passwordEncoder) {
        String hashedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
        createUserIfMissing(ADMIN_EMAIL, "Admin", "User", "admin", hashedPassword);
        createUserIfMissing(USER_EMAIL, "Normal", "User", "user", hashedPassword);
    }

    private void createUserIfMissing(String email, String firstName, String lastName,
            String username, String hashedPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(firstName + " " + lastName)
                .username(username)
                .password(hashedPassword)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        log.info("Created user: {} (default password: {})", email, DEFAULT_PASSWORD);
    }

    private void assignRolePermissions() {
        Role adminRole = roleRepository.findByName(ADMIN_ROLE).orElseThrow();

        for (String code : ADMIN_PERMISSIONS) {
            Permission permission = permissionRepository.findByCode(code).orElseThrow();
            if (rolePermissionRepository.findByRoleAndPermission(adminRole, permission).isEmpty()) {
                RolePermission rolePermission = RolePermission.builder()
                        .role(adminRole)
                        .permission(permission)
                        .build();
                rolePermissionRepository.save(rolePermission);
            }
        }
        log.info("Assigned {} permissions to role {}", ADMIN_PERMISSIONS.size(), ADMIN_ROLE);
    }

    private void assignUserRoles() {
        assignRoleToUser(ADMIN_EMAIL, ADMIN_ROLE);
        assignRoleToUser(USER_EMAIL, USER_ROLE);
    }

    private void assignRoleToUser(String email, String roleName) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Role role = roleRepository.findByName(roleName).orElseThrow();

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            return;
        }
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        userRoleRepository.save(userRole);
        log.info("Assigned role {} to user {}", roleName, email);
    }

    private String buildPermissionDescription(String code) {
        int lastUnderscore = code.lastIndexOf('_');
        if (lastUnderscore < 0) {
            return code;
        }
        String resource = code.substring(0, lastUnderscore).toLowerCase().replace('_', ' ');
        String action = code.substring(lastUnderscore + 1);
        return Character.toUpperCase(action.charAt(0)) + action.substring(1).toLowerCase()
                + " " + resource;
    }
}