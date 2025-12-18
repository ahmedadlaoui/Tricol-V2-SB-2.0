package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.GoodsIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsIssueRepository extends JpaRepository<GoodsIssue, Long> {
    Optional<GoodsIssue> findByIssueNumber(String issueNumber);

    List<GoodsIssue> findByStatus(GoodsIssueStatus status);
}
