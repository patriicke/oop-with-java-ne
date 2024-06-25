package com.supamenu.www.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionsResponseDTO {
    List<TransactionResponseDTO> transactions;
}
