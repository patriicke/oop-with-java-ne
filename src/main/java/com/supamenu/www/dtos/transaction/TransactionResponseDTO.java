package com.supamenu.www.dtos.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class TransactionResponseDTO {
    private final String accountNumber;
    private final double transactionAmount;
    private final String description;
    private final String transactionType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private final LocalDateTime createdAt;
    private final double accountBalance;
}
