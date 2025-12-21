package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.goodsissue.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.BusinessViolationException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.GoodsIssueMapper;
import com.example.tricolv2sb.Repository.*;
import com.example.tricolv2sb.Service.ServiceInterfaces.GoodsIssueServiceInterface;
import com.example.tricolv2sb.Util.interfaces.currentUserGetterInterface;
import com.example.tricolv2sb.Util.interfaces.eventPublisherUtilInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoodsIssueService implements GoodsIssueServiceInterface {

    private final GoodsIssueRepository goodsIssueRepository;
    private final GoodsIssueLineRepository goodsIssueLineRepository;
    private final ProductRepository productRepository;
    private final StockLotRepository stockLotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final GoodsIssueMapper goodsIssueMapper;
    private final eventPublisherUtilInterface eventPublisherUtilInterface;
    private final currentUserGetterInterface userGetter;

    @Transactional(readOnly = true)
    public List<ReadGoodsIssueDTO> fetchAllGoodsIssues() {
        List<GoodsIssue> goodsIssues = goodsIssueRepository.findAll();
        return goodsIssues.stream()
                .map(goodsIssueMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadGoodsIssueDTO> fetchGoodsIssuesByStatus(GoodsIssueStatus status) {
        return goodsIssueRepository.findByStatus(status).stream()
                .map(goodsIssueMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReadGoodsIssueDTO> fetchGoodsIssueById(Long id) {
        return Optional.of(
                goodsIssueRepository.findById(id)
                        .map(goodsIssueMapper::toDto)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Goods issue not found with ID: " + id)));
    }

    @Transactional
    public ReadGoodsIssueDTO createGoodsIssue(CreateGoodsIssueDTO dto) {
        GoodsIssue goodsIssue = goodsIssueMapper.toEntity(dto);

        String issueNumber = generateIssueNumber();
        goodsIssue.setIssueNumber(issueNumber);
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);

        List<GoodsIssueLine> issueLines = new ArrayList<>();
        for (var lineDto : dto.getIssueLines()) {
            Product product = productRepository.findById(lineDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + lineDto.getProductId()));

            GoodsIssueLine line = new GoodsIssueLine();
            line.setProduct(product);
            line.setQuantity(lineDto.getQuantity());
            line.setGoodsIssue(goodsIssue);
            issueLines.add(line);
        }

        goodsIssue.setIssueLines(issueLines);
        GoodsIssue savedGoodsIssue = goodsIssueRepository.save(goodsIssue);

        UserApp currentUser = userGetter.getCurrentUser();
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put("GoodsIssue id", String.valueOf(savedGoodsIssue.getId()));

        eventPublisherUtilInterface.triggerAuditLogEventPublisher("GOODSISSUE_CREATED", currentUser, additionalDetails);


        return goodsIssueMapper.toDto(savedGoodsIssue);
    }

    @Transactional
    public ReadGoodsIssueDTO updateGoodsIssue(Long id, UpdateGoodsIssueDTO dto) {
        GoodsIssue existingGoodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods issue not found with ID: " + id));

        if (existingGoodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new BusinessViolationException(
                    "Cannot update goods issue with status: " + existingGoodsIssue.getStatus());
        }

        goodsIssueMapper.updateFromDto(dto, existingGoodsIssue);
        GoodsIssue savedGoodsIssue = goodsIssueRepository.save(existingGoodsIssue);
        return goodsIssueMapper.toDto(savedGoodsIssue);
    }

    @Transactional
    public void deleteGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods issue not found with ID: " + id));

        if (goodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new BusinessViolationException(
                    "Only DRAFT goods issues can be deleted. Current status: " + goodsIssue.getStatus());
        }

        goodsIssueRepository.deleteById(id);

        UserApp currentUser = userGetter.getCurrentUser();
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put("GoodsIssue id", String.valueOf(goodsIssue.getId()));
        additionalDetails.put("GoodsIssue status", String.valueOf(goodsIssue.getStatus()));
        additionalDetails.put("GoodsIssue lines", String.valueOf(goodsIssue.getIssueLines()));

        eventPublisherUtilInterface.triggerAuditLogEventPublisher("GOODSISSUE_DELETED", currentUser, additionalDetails);
    }

    @Transactional
    public void validateGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods issue not found with ID: " + id));

        if (goodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new BusinessViolationException(
                    "Only DRAFT goods issues can be validated. Current status: " + goodsIssue.getStatus());
        }

        List<GoodsIssueLine> issueLines = goodsIssueLineRepository.findByGoodsIssueId(id);

        if (issueLines.isEmpty()) {
            throw new BusinessViolationException("Cannot validate goods issue without issue lines");
        }

        for (GoodsIssueLine line : issueLines) {
            processGoodsIssueLineFIFO(line);
        }

        goodsIssue.setStatus(GoodsIssueStatus.VALIDATED);
        goodsIssueRepository.save(goodsIssue);

        UserApp currentUser = userGetter.getCurrentUser();
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put("GoodsIssue id", String.valueOf(goodsIssue.getId()));

        eventPublisherUtilInterface.triggerAuditLogEventPublisher("GOODSISSUE_VALIDATED", currentUser, additionalDetails);
    }

    private void processGoodsIssueLineFIFO(GoodsIssueLine line) {
        Long productId = line.getProduct().getId();
        Double reorderPoint = line.getProduct().getReorderPoint();
        Double requiredQuantity = line.getQuantity();

        Double availableStock = stockLotRepository.calculateTotalAvailableStock(productId);
        if (availableStock < requiredQuantity) {
            throw new BusinessViolationException(
                    String.format("Insufficient stock for product ID %d. Required: %.2f, Available: %.2f",
                            productId, requiredQuantity, availableStock));
        }

        Double projectedStockAfterIssue = availableStock - requiredQuantity;
        if (projectedStockAfterIssue <= reorderPoint) {
            throw new BusinessViolationException(
                    String.format("Stock would fall below reorder point for product ID %d. " +
                            "Reorder Point: %.2f, Stock After Issue: %.2f",
                            productId, reorderPoint, projectedStockAfterIssue));
        }

        List<StockLot> availableLots = stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(productId);

        Double remainingToConsume = requiredQuantity;

        for (StockLot lot : availableLots) {
            if (remainingToConsume <= 0) {
                break;
            }

            Double lotAvailable = lot.getRemainingQuantity();
            Double quantityToConsume = Math.min(lotAvailable, remainingToConsume);

            lot.setRemainingQuantity(lotAvailable - quantityToConsume);
            stockLotRepository.save(lot);

            StockMovement movement = new StockMovement();
            movement.setMovementType(StockMovementType.OUT);
            movement.setQuantity(quantityToConsume);
            movement.setMovementDate(LocalDate.now());
            movement.setProduct(line.getProduct());
            movement.setStockLot(lot);
            movement.setGoodsIssueLine(line);
            stockMovementRepository.save(movement);

            remainingToConsume -= quantityToConsume;
        }

        if (remainingToConsume > 0.001) {
            throw new BusinessViolationException(
                    String.format("Failed to consume required quantity for product ID %d. Remaining: %.2f",
                            productId, remainingToConsume));
        }


    }

    @Transactional
    public void cancelGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods issue not found with ID: " + id));

        if (goodsIssue.getStatus() == GoodsIssueStatus.CANCELLED) {
            throw new BusinessViolationException("Goods issue is already cancelled");
        }

        goodsIssue.setStatus(GoodsIssueStatus.CANCELLED);
        goodsIssueRepository.save(goodsIssue);

        UserApp currentUser = userGetter.getCurrentUser();
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put("GoodsIssue id", String.valueOf(goodsIssue.getId()));

        eventPublisherUtilInterface.triggerAuditLogEventPublisher("GOODSISSUE_CANCELED", currentUser, additionalDetails);
    }

    private String generateIssueNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = goodsIssueRepository.count() + 1;
        return String.format("GI-%s-%03d", date, count);
    }
}
