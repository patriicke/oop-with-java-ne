package com.supamenu.www.controllers;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.DepositTransactionDTO;
import com.supamenu.www.dtos.transaction.TransactionResponseDTO;
import com.supamenu.www.dtos.transaction.TransactionsResponseDTO;
import com.supamenu.www.services.interfaces.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/deposit-to-your-account")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(@Valid @RequestBody DepositTransactionDTO depositTransactionDTO) {
        return transactionService.depositToYourAccount(depositTransactionDTO);
    }

    @GetMapping("/get-my-transactions")
    public ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions(){
        return transactionService.getMyTransactions();
    }
}
