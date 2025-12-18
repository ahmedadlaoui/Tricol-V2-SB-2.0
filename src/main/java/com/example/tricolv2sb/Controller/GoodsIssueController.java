package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.goodsissue.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Service.ServiceInterfaces.GoodsIssueServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goods-issues")
public class GoodsIssueController {

    private final GoodsIssueServiceInterface goodsIssueService;

    @GetMapping
    @PreAuthorize("hasAuthority('GOODS_ISSUE:READ')")
    public ResponseEntity<ApiResponse<List<ReadGoodsIssueDTO>>> getAllGoodsIssues() {
        List<ReadGoodsIssueDTO> goodsIssues = goodsIssueService.fetchAllGoodsIssues();
        return ResponseEntity.ok(ApiResponse.success(goodsIssues, "Goods issues fetched successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_ISSUE:READ')")
    public ResponseEntity<ApiResponse<ReadGoodsIssueDTO>> getGoodsIssueById(@PathVariable Long id) {
        return goodsIssueService.fetchGoodsIssueById(id)
                .map(res -> ResponseEntity.ok(ApiResponse.success(res, "Goods issue fetched successfully")))
                .orElseThrow(() -> new ResourceNotFoundException("Goods issue not found with ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GOODS_ISSUE:CREATE')")
    public ResponseEntity<ApiResponse<ReadGoodsIssueDTO>> createGoodsIssue(
            @Valid @RequestBody CreateGoodsIssueDTO dto) {
        ReadGoodsIssueDTO newGoodsIssue = goodsIssueService.createGoodsIssue(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(newGoodsIssue, "Goods issue created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_ISSUE:UPDATE')")
    public ResponseEntity<ApiResponse<ReadGoodsIssueDTO>> updateGoodsIssue(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoodsIssueDTO dto) {
        ReadGoodsIssueDTO updatedGoodsIssue = goodsIssueService.updateGoodsIssue(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedGoodsIssue, "Goods issue updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_ISSUE:DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteGoodsIssue(@PathVariable Long id) {
        goodsIssueService.deleteGoodsIssue(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Goods issue deleted successfully"));
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasAuthority('GOODS_ISSUE:VALIDATE')")
    public ResponseEntity<ApiResponse<Void>> validateGoodsIssue(@PathVariable Long id) {
        goodsIssueService.validateGoodsIssue(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Goods issue validated successfully"));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('GOODS_ISSUE:CANCEL')")
    public ResponseEntity<ApiResponse<Void>> cancelGoodsIssue(@PathVariable Long id) {
        goodsIssueService.cancelGoodsIssue(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Goods issue cancelled successfully"));
    }
}
