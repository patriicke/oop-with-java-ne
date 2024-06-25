package com.supamenu.www.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponseDTO {
    private String accountName;
    private String accountNumber;
    private Double accountBalance;
}
