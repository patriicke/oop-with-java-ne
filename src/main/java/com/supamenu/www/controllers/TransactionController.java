package com.supamenu.www.controllers;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.*;
import com.supamenu.www.services.interfaces.TransactionService;
import com.supamenu.www.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/deposit-to-your-account")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(@Valid @RequestBody DepositTransactionDTO depositTransactionDTO) {
        return transactionService.depositToYourAccount(depositTransactionDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/get-all-transactions")
    public ResponseEntity<ApiResponse<PaginatedTransactionsDTO>> getAllTransactions(
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page
            ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return transactionService.getAllTransactions(pageable);
    }

    @GetMapping("/get-my-transactions")
    public ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions(
    ) {
        return transactionService.getMyTransactions();
    }

    @PostMapping("/send-to-other-accounts")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> sendToOtherAccounts(@Valid @RequestBody WithDrawTransactionDTO withDrawTransactionDTO) {
        return transactionService.withDrawToOtherAccounts(withDrawTransactionDTO);
    }
}
