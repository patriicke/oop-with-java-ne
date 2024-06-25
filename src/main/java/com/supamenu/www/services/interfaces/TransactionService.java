package com.supamenu.www.services.interfaces;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.*;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TransactionService {

    /**
     * Creates a new transaction entity based on the provided account and transaction details.
     *
     * @param account The account associated with the transaction.
     * @param createTransactionDTO The DTO containing transaction details.
     * @return The created Transaction entity.
     */
    Transaction createTransactionEntity(Account account, CreateTransactionDTO createTransactionDTO);

    /**
     * Handles the deposit transaction to the logged-in user's account.
     *
     * @param depositTransactionDTO The DTO containing deposit transaction details.
     * @return A ResponseEntity containing the ApiResponse with the transaction details.
     */
    ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(DepositTransactionDTO depositTransactionDTO);

    /**
     * Retrieves all transactions for the logged-in user.
     *
     * @return A ResponseEntity containing the ApiResponse with the list of transactions.
     */
    ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions();

    /**
     * Handles the withdrawal transaction from the logged-in user's account to another account.
     *
     * @param createTransactionDTO The DTO containing withdrawal transaction details.
     * @return A ResponseEntity containing the ApiResponse with the transaction details.
     */
    ResponseEntity<ApiResponse<TransactionResponseDTO>> withDrawToOtherAccounts(WithDrawTransactionDTO createTransactionDTO);

    /**
     * Retrieves all transactions for the logged-in user.
     *
     * @param pageable The pageable object containing pagination details.
     * @return A ResponseEntity containing the ApiResponse with the list of transactions.
     */
    ResponseEntity<ApiResponse<PaginatedTransactionsDTO>> getAllTransactions(Pageable pageable);
}
