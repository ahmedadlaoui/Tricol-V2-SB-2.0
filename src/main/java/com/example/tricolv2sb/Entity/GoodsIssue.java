package com.example.tricolv2sb.Entity;

import com.example.tricolv2sb.Entity.Enum.GoodsIssueMotif;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "goods_issues")
@Getter
@Setter
public class GoodsIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String issueNumber;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoodsIssueMotif motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoodsIssueStatus status;

    @Column(nullable = true)
    private Double totalAmount;

    @OneToMany(mappedBy = "goodsIssue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GoodsIssueLine> issueLines = new ArrayList<>();

    public void calculateTotalAmount() {
        if (issueLines != null && !issueLines.isEmpty()) {
            this.totalAmount = issueLines.stream()
                    .filter(line -> line.getLineTotal() != null)
                    .mapToDouble(GoodsIssueLine::getLineTotal)
                    .sum();
        } else {
            this.totalAmount = 0.0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GoodsIssue that = (GoodsIssue) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}