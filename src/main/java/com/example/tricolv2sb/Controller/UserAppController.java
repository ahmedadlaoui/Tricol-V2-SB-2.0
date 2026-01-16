package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.DTO.userapp.UserPermissionsDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAppController {

    private final UserAppServiceInterface userAppService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<ApiResponse<Page<ReadUserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReadUserDTO> users = userAppService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully"));
    }

    @GetMapping("/{id}/all-permissions")
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<ApiResponse<UserPermissionsDTO>> getUserPermissions(@PathVariable Long id) {
        UserPermissionsDTO permissions = userAppService.getUserPermissions(id);
        return ResponseEntity.ok(ApiResponse.success(permissions, "User permissions fetched successfully"));
    }

    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleDTO assignRoleDTO) {
        userAppService.assignRoleToUser(id, assignRoleDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "Role assigned successfully"));
    }
}
