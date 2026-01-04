package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.PermissionDTO.CreatePermissionDTO;
import com.example.tricolv2sb.DTO.PermissionDTO.ReadPermissionDTO;
import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.Permission;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Entity.UserPermission;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Repository.PermissionRepository;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Repository.UserPermissionRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.CustomPermissionService;
import com.example.tricolv2sb.Util.interfaces.currentUserGetterInterface;
import com.example.tricolv2sb.Util.interfaces.eventPublisherUtilInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomPermissionServiceImpl implements CustomPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserAppRepository userAppRepository;
    private final PermissionRepository permissionRepository;
    private final currentUserGetterInterface currentUserGetter;
    private final eventPublisherUtilInterface eventPublisher;

    @Override
    @Transactional
    public ReadPermissionDTO assignPermission(Long userId, CreatePermissionDTO dto) {
        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Permission permission = permissionRepository.findById(dto.getPermissionId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Permission not found with ID: " + dto.getPermissionId()));

        // Check if user already has this custom permission
        Optional<UserPermission> existingPermission = userPermissionRepository
                .findByUserIdAndPermissionId(userId, dto.getPermissionId());

        UserPermission userPermission;
        if (existingPermission.isPresent()) {
            // Update existing permission
            userPermission = existingPermission.get();
            userPermission.setGranted(dto.getIsGranted());
            userPermission.setGrantedAt(LocalDateTime.now());
            userPermission.setGrantedBy(getCurrentUserEmail());
        } else {
            // Create new permission
            userPermission = UserPermission.builder()
                    .user(user)
                    .permission(permission)
                    .isGranted(dto.getIsGranted())
                    .grantedBy(getCurrentUserEmail())
                    .build();
        }

        UserPermission savedPermission = userPermissionRepository.save(userPermission);

        // Trigger audit log
        triggerAuditLog(user, permission, dto.getIsGranted());

        return mapToReadPermissionDTO(savedPermission);
    }

    @Override
    @Transactional
    public void revokePermission(Long userId, Long permissionId) {
        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        UserPermission userPermission = userPermissionRepository
                .findByUserIdAndPermissionId(userId, permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Custom permission not found for user " + userId + " and permission " + permissionId));

        userPermissionRepository.delete(userPermission);

        // Trigger audit log
        Map<String, String> details = new HashMap<>();
        details.put("targetUserId", userId.toString());
        details.put("targetUserEmail", user.getEmail());
        details.put("permissionId", permissionId.toString());
        details.put("permissionCode", userPermission.getPermission().getAuthority());
        details.put("action", "REVOKED");
        eventPublisher.triggerAuditLogEventPublisher(ActionName.PERMISSION_ASSIGNED, user, details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadPermissionDTO> getUserCustomPermissions(Long userId) {
        if (!userAppRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<UserPermission> permissions = userPermissionRepository.findByUserId(userId);
        return permissions.stream()
                .map(this::mapToReadPermissionDTO)
                .collect(Collectors.toList());
    }

    private ReadPermissionDTO mapToReadPermissionDTO(UserPermission userPermission) {
        return ReadPermissionDTO.builder()
                .id(userPermission.getId())
                .userId(userPermission.getUser().getId())
                .userEmail(userPermission.getUser().getEmail())
                .permissionId(userPermission.getPermission().getId())
                .permissionCode(userPermission.getPermission().getAuthority())
                .permissionDescription(userPermission.getPermission().getDescription())
                .isGranted(userPermission.isGranted())
                .grantedAt(userPermission.getGrantedAt())
                .grantedBy(userPermission.getGrantedBy())
                .build();
    }

    private String getCurrentUserEmail() {
        UserApp currentUser = currentUserGetter.getCurrentUser();
        return currentUser != null ? currentUser.getEmail() : "SYSTEM";
    }

    private void triggerAuditLog(UserApp targetUser, Permission permission, boolean isGranted) {
        Map<String, String> details = new HashMap<>();
        details.put("targetUserId", targetUser.getId().toString());
        details.put("targetUserEmail", targetUser.getEmail());
        details.put("permissionId", permission.getId().toString());
        details.put("permissionCode", permission.getAuthority());
        details.put("action", isGranted ? "GRANTED" : "DENIED");
        eventPublisher.triggerAuditLogEventPublisher(ActionName.PERMISSION_ASSIGNED, targetUser, details);
    }
}
