package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Entity.Enum.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleAppRepository extends JpaRepository<RoleApp, Long> {
    Optional<RoleApp> findByName(RoleName name);

    @Query("SELECT DISTINCT r FROM RoleApp r LEFT JOIN FETCH r.permissions")
    List<RoleApp> findAllWithPermissions();
}

