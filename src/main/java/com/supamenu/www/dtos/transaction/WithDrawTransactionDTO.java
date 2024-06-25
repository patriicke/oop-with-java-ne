package com.supamenu.www.dtos.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class WithDrawTransactionDTO {
    @Schema(description = "From Account number", example = "123456789012")
    @NotBlank(message = "Account Number cannot be blank")
    @Pattern(regexp = "^\\d{12}$", message = "From Account Number should be 12 digits long")
    private String fromAccountNumber;

    @Schema(description = "To Account number", example = "123456789012")
    @NotBlank(message = "Account Number cannot be blank")
    @Pattern(regexp = "^\\d{12}$", message = "To Account Number should be 12 digits long")
    private String toAccountNumber;

    @Schema(description = "Transaction amount", example = "1000.0")
    @Min(value = 1, message = "Transaction Amount must be greater than one")
    private double transactionAmount;

    @Schema(description = "Transaction description", example = "Deposit transaction")
    @NotBlank(message = "Transaction Description cannot be blank")
    private String description;
}
