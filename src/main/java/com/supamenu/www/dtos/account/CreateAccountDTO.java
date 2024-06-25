package com.supamenu.www.dtos.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {
    @Schema(description = "Account name", example = "Savings Account")
    @NotBlank(message = "Account name cannot be blank")
    private String accountName;
}
