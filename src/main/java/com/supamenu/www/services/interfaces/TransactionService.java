package com.supamenu.www.services.interfaces;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.CreateTransactionDTO;
import com.supamenu.www.dtos.transaction.DepositTransactionDTO;
import com.supamenu.www.dtos.transaction.TransactionResponseDTO;
import com.supamenu.www.dtos.transaction.TransactionsResponseDTO;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.Transaction;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    public Transaction createTransactionEntity(Account account, CreateTransactionDTO createTransactionDTO);

    public ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(DepositTransactionDTO depositTransactionDTO);

    public ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions();
}
