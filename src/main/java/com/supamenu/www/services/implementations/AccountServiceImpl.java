package com.supamenu.www.services.implementations;

import com.supamenu.www.dtos.account.AccountResponseDTO;
import com.supamenu.www.dtos.account.AccountsResponseDTO;
import com.supamenu.www.dtos.account.CreateAccountDTO;
import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.exceptions.CustomException;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.User;
import com.supamenu.www.repositories.IAccountRepository;
import com.supamenu.www.services.interfaces.AccountService;
import com.supamenu.www.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserService userService;
    private final IAccountRepository accountRepository;

    public Long generateAccountNumber() {
        Random generator = new Random(System.currentTimeMillis());
        return Math.abs(generator.nextLong()) % 1000000000000L;
    }

    @Override
    public Account createAccountEntity(User user, CreateAccountDTO createUserDTO) {
        Account account = new Account();
        account.setAccountName(createUserDTO.getAccountName());
        account.setAccountNumber(generateAccountNumber().toString());
        account.setUser(user);
        return account;
    }

    @Override
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(CreateAccountDTO createUserDTO) {
        try{
            User user = userService.getLoggedInUser();
            Account account = createAccountEntity(user, createUserDTO);
            accountRepository.save(account);
            return ApiResponse.success("Successfully created account", HttpStatus.CREATED, new AccountResponseDTO(account.getAccountName(), account.getAccountNumber(), account.getAccountBalance()));
        }catch (Exception e){
            throw new CustomException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<AccountsResponseDTO>> getMyAccounts() {
        try{
            User user = userService.getLoggedInUser();
            Set<Account> accounts = accountRepository.findAccountByUser(user);
            Set<AccountResponseDTO> accountsResponseDTO = new HashSet<>();
            for(Account account: accounts){
                accountsResponseDTO.add(new AccountResponseDTO(account.getAccountName(), account.getAccountNumber(), account.getAccountBalance()));
            }
            return ApiResponse.success("Successfully fetched accounts", HttpStatus.OK, new AccountsResponseDTO(accountsResponseDTO));
        }catch (Exception e){
            throw new CustomException(e);
        }
    }
}
