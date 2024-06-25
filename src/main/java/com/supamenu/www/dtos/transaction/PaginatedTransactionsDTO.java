package com.supamenu.www.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedTransactionsDTO {
    Page<TransactionResponseDTO> transactions;
}
