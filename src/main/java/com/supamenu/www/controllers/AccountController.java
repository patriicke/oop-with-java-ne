package com.supamenu.www.controllers;

import com.supamenu.www.dtos.account.AccountResponseDTO;
import com.supamenu.www.dtos.account.AccountsResponseDTO;
import com.supamenu.www.dtos.account.CreateAccountDTO;
import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.services.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create-account")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(@Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return accountService.createAccount(createAccountDTO);
    }

    @GetMapping("/get-my-accounts")
    public ResponseEntity<ApiResponse<AccountsResponseDTO>> getMyAccounts() {
        return accountService.getMyAccounts();
    }
}
