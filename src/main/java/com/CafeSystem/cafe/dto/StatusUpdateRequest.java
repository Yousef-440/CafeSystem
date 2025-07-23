package com.CafeSystem.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusUpdateRequest {
    @Schema(description = "Status", example = " ACTIVE, INACTIVE, PENDING, BLOCKED")
    private String status;
    @Schema(description = "The user ID number you want to update", example = "1")
    private Integer id;
    @Schema(description = "The Token of the user who is making the modification", example = "exdfred....")
    private String token;
}
