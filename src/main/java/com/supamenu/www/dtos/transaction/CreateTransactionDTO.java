package com.supamenu.www.dtos.transaction;

import com.supamenu.www.enumerations.transaction.ETransactionStatus;
import com.supamenu.www.enumerations.transaction.ETransactionType;
import com.supamenu.www.models.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionDTO {
    private ETransactionType transactionType;
    private double transactionAmount;
    private String description;
    private ETransactionStatus transactionStatus;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double accountBalance;
}
