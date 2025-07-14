package com.CafeSystem.cafe.dto.bill;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BillRequestDTO {
    @NotBlank(message = "Please fill in the name field")
    private String name;

    @NotBlank(message = "Please fill in the email field")
    private String email;

    @NotBlank(message = "Please fill in the concatNumber field")
    private String contactNumber;

    @NotBlank(message = "Please fill in the paymentMethod field")
    private String paymentMethod;

    private List<ProductDetailsDTO> productDetails;

    @Positive
    @Digits(integer = 4, fraction = 2)
    private Double totalAmount;
}
