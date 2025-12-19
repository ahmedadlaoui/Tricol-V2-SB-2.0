package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByEmail(String email);

    @Query("SELECT u FROM UserApp u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH r.permissions " +
            "LEFT JOIN FETCH u.customPermissions cp " +
            "LEFT JOIN FETCH cp.permission " +
            "WHERE u.email = :email")
    Optional<UserApp> findByEmailWithAuthorities(@Param("email") String email);
}
