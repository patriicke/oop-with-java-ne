package com.supamenu.www.services.interfaces;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.*;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.Transaction;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    public Transaction createTransactionEntity(Account account, CreateTransactionDTO createTransactionDTO);

    public ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(DepositTransactionDTO depositTransactionDTO);

    public ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions();

    public ResponseEntity<ApiResponse<TransactionResponseDTO>> withDrawToOtherAccounts(WithDrawTransactionDTO createTransactionDTO);
}
