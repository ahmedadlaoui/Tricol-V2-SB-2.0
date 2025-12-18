package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.goodsissue.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;

import java.util.List;
import java.util.Optional;

public interface GoodsIssueServiceInterface {

    List<ReadGoodsIssueDTO> fetchAllGoodsIssues();

    List<ReadGoodsIssueDTO> fetchGoodsIssuesByStatus(GoodsIssueStatus status);

    Optional<ReadGoodsIssueDTO> fetchGoodsIssueById(Long id);

    ReadGoodsIssueDTO createGoodsIssue(CreateGoodsIssueDTO dto);

    ReadGoodsIssueDTO updateGoodsIssue(Long id, UpdateGoodsIssueDTO dto);

    void deleteGoodsIssue(Long id);

    void validateGoodsIssue(Long id);

    void cancelGoodsIssue(Long id);
}
