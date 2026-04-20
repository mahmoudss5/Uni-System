package UnitSystem.demo.Controllers;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.PermissionService;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.PermissionResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Permission.UserPermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission", description = "Endpoints for permission management")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Get all permissions")
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(summary = "Get permission by ID")
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Long permissionId) {
        return ResponseEntity.ok(permissionService.getPermissionById(permissionId));
    }

    @Operation(summary = "Create a new permission")
    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(request));
    }

    @Operation(summary = "Update permission")
    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> updatePermission(
            @PathVariable Long permissionId,
            @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.updatePermission(permissionId, request));
    }

    @Operation(summary = "Delete permission")
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign or update user permission")
    @PostMapping("/users")
    public ResponseEntity<UserPermissionResponse> assignPermissionToUser(@RequestBody UserPermissionRequest request) {
        return ResponseEntity.ok(permissionService.assignPermissionToUser(request));
    }

    @Operation(summary = "Get permissions for user")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserPermissionResponse>> getUserPermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(permissionService.getUserPermissions(userId));
    }

    @Operation(summary = "Remove specific permission from user")
    @DeleteMapping("/users/{userId}/{permissionId}")
    public ResponseEntity<Void> removePermissionFromUser(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {
        permissionService.removePermissionFromUser(userId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
