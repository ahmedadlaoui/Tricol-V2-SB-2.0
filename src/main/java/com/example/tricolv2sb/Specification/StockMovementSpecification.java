package com.example.tricolv2sb.Specification;

import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Entity.StockMovement;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockMovementSpecification {

    /**
     * Filtre par période (date de début et date de fin)
     */
    public static Specification<StockMovement> hasDateBetween(LocalDate dateDebut, LocalDate dateFin) {
        return (root, query, criteriaBuilder) -> {
            if (dateDebut == null && dateFin == null) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            if (dateDebut != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("movementDate"), dateDebut));
            }

            if (dateFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("movementDate"), dateFin));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filtre par ID de produit
     */
    public static Specification<StockMovement> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("id"), productId);
        };
    }

    /**
     * Filtre par référence de produit
     */
    public static Specification<StockMovement> hasProductReference(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("reference"), reference);
        };
    }

    /**
     * Filtre par type de mouvement (IN ou OUT)
     */
    public static Specification<StockMovement> hasMovementType(StockMovementType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("movementType"), type);
        };
    }

    /**
     * Filtre par numéro de lot
     */
    public static Specification<StockMovement> hasLotNumber(String lotNumber) {
        return (root, query, criteriaBuilder) -> {
            if (lotNumber == null || lotNumber.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("stockLot").get("lotNumber"), lotNumber);
        };
    }


    public static Specification<StockMovement> buildSearchSpecification(
            LocalDate dateDebut,
            LocalDate dateFin,
            Long productId,
            String reference,
            StockMovementType type,
            String lotNumber) {

        Specification<StockMovement> spec = Specification.allOf();

        if (dateDebut != null || dateFin != null) {
            spec = spec.and(hasDateBetween(dateDebut, dateFin));
        }
        if (productId != null) {
            spec = spec.and(hasProductId(productId));
        }
        if (reference != null && !reference.trim().isEmpty()) {
            spec = spec.and(hasProductReference(reference));
        }
        if (type != null) {
            spec = spec.and(hasMovementType(type));
        }
        if (lotNumber != null && !lotNumber.trim().isEmpty()) {
            spec = spec.and(hasLotNumber(lotNumber));
        }

        return spec;
    }
}
