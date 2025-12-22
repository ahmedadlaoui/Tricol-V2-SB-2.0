package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.UserAppMapper;
import com.example.tricolv2sb.Repository.RoleAppRepository;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import com.example.tricolv2sb.Util.interfaces.eventPublisherUtilInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAppService implements UserAppServiceInterface {

    private final UserAppRepository userRepository;
    private final RoleAppRepository roleRepository;
    private final UserAppMapper userAppMapper;
    private final eventPublisherUtilInterface eventPublisherUtilInterface;

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        RoleApp role = roleRepository.findByName(assignRoleDTO.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + assignRoleDTO.getRoleName()));

        user.setRole(role);
        userRepository.save(user);
        eventPublisherUtilInterface.triggerAuditLogEventPublisher(ActionName.ROLE_ASSIGNED, user);

    }

    @Override
    public List<ReadUserDTO> getAllUsers() {
        List<UserApp> users = userRepository.findAll();
        return userAppMapper.toReadUserDTOList(users);
    }
}
