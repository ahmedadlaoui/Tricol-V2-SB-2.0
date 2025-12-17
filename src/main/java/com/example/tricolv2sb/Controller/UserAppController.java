package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAppController {

    private final UserAppServiceInterface userAppService;

    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<String> assignRoleToUser(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleDTO assignRoleDTO) {
        userAppService.assignRoleToUser(id, assignRoleDTO);
        return ResponseEntity.ok("Role assigned successfully to user " + id);
    }
}

