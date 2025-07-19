package com.CafeSystem.cafe.model;

import com.CafeSystem.cafe.enumType.RoleType;
import com.CafeSystem.cafe.enumType.StatusType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "cafe_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Column(unique = true)
    private String email;

    private String contactNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar default 'ACTIVE'")
    private StatusType status;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy, HH:mm")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy, HH:mm")
    private LocalDateTime modifiedAt;
}
