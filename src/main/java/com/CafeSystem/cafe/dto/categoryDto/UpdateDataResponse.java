package com.CafeSystem.cafe.dto.categoryDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
public class UpdateDataResponse {
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedAt;
}
