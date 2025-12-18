package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<List<ReadUserDTO>>> getAllUsers() {
        List<ReadUserDTO> users = userAppService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully"));
    }

    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleDTO assignRoleDTO) {
        userAppService.assignRoleToUser(id, assignRoleDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "Role assigned successfully to user " + id));
    }
}
