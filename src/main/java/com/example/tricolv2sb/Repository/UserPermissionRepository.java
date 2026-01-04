package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);

    List<UserPermission> findByUserId(Long userId);

    void deleteByUserIdAndPermissionId(Long userId, Long permissionId);
}
