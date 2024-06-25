package com.supamenu.www.services.interfaces;

import com.supamenu.www.dtos.account.AccountResponseDTO;
import com.supamenu.www.dtos.account.AccountsResponseDTO;
import com.supamenu.www.dtos.account.CreateAccountDTO;
import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.User;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    public Long generateAccountNumber();

    public Account createAccountEntity(User user, CreateAccountDTO createUserDTO);

    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(CreateAccountDTO createUserDTO);

    public ResponseEntity<ApiResponse<AccountsResponseDTO>> getMyAccounts();

}
