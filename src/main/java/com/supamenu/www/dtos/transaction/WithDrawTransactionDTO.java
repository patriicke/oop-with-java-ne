package com.supamenu.www.dtos.transaction;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class WithDrawTransactionDTO {
    private final String accountNumber;
    private final double transactionAmount;
    private final String description;
}
