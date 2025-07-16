package com.CafeSystem.cafe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;

import org.hibernate.type.SqlTypes;


import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@DynamicInsert
@DynamicUpdate
@Builder
@Entity

public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;

    private String name;

    private String email;

    private String contactNumber;

    private String paymentMethod;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_details", columnDefinition = "jsonb")
    private String productDetails;

    private String createdBy;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy, HH:mm")
    private LocalDateTime createdAt;

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

}
