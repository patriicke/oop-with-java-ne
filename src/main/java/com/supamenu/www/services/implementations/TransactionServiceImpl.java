package com.supamenu.www.services.implementations;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.*;
import com.supamenu.www.dtos.user.CreateUserDTO;
import com.supamenu.www.dtos.user.UserResponseDTO;
import com.supamenu.www.enumerations.transaction.ETransactionStatus;
import com.supamenu.www.enumerations.transaction.ETransactionType;
import com.supamenu.www.exceptions.BadRequestException;
import com.supamenu.www.exceptions.CustomException;
import com.supamenu.www.exceptions.NotFoundException;
import com.supamenu.www.models.Account;
import com.supamenu.www.models.Transaction;
import com.supamenu.www.models.User;
import com.supamenu.www.repositories.IAccountRepository;
import com.supamenu.www.repositories.ITransactionRepository;
import com.supamenu.www.services.interfaces.MailService;
import com.supamenu.www.services.interfaces.TransactionService;
import com.supamenu.www.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final UserService userService;
    private final TemplateEngine templateEngine;
    private final MailService mailService;

    @Override
    public Transaction createTransactionEntity(Account account, CreateTransactionDTO createTransactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(createTransactionDTO.getTransactionAmount());
        transaction.setDescription(createTransactionDTO.getDescription());
        transaction.setTransactionStatus(ETransactionStatus.PENDING);
        transaction.setAccount(account);
        transaction.setFromAccountNumber(createTransactionDTO.getFromAccountNumber());
        transaction.setToAccountNumber(createTransactionDTO.getToAccountNumber());
        transaction.setAccountBalance(createTransactionDTO.getAccountBalance());
        transaction.setTransactionType(createTransactionDTO.getTransactionType());
        return transaction;
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> depositToYourAccount(DepositTransactionDTO depositTransactionDTO) {
        try {
            User user = userService.getLoggedInUser();
            Account account = accountRepository.findAccountByAccountNumber(depositTransactionDTO.getAccountNumber());

            if (account == null)
                throw new NotFoundException("Account not found");

            if (!user.getId().equals(account.getUser().getId())) {
                throw new BadRequestException("You can not deposit to account which is not yours");
            }

            CreateTransactionDTO createTransactionDTO = new CreateTransactionDTO();
            createTransactionDTO.setTransactionAmount(depositTransactionDTO.getTransactionAmount());
            createTransactionDTO.setDescription(depositTransactionDTO.getDescription());
            createTransactionDTO.setFromAccountNumber("N/A");
            createTransactionDTO.setToAccountNumber(depositTransactionDTO.getAccountNumber());
            createTransactionDTO.setTransactionType(ETransactionType.DEPOSIT);
            createTransactionDTO.setAccountBalance(account.getAccountBalance() + depositTransactionDTO.getTransactionAmount());

            Transaction transaction = createTransactionEntity(account, createTransactionDTO);

            account.setAccountBalance(account.getAccountBalance() + depositTransactionDTO.getTransactionAmount());
            accountRepository.save(account);

            transaction.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
            transactionRepository.save(transaction);

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("amount", transaction.getTransactionAmount());
            context.setVariable("accountNumber", transaction.getToAccountNumber());
            context.setVariable("accountName", account.getAccountName());
            context.setVariable("balance", transaction.getAccountBalance());

            String content = templateEngine.process("deposit-transaction-email.html", context);
            mailService.sendEmail(user.getEmail(), "Deposit Successful", content, true);
            return ApiResponse.success(
                    "Successfully deposited to your account",
                    HttpStatus.CREATED,
                    new TransactionResponseDTO(
                            transaction.getFromAccountNumber(),
                            transaction.getToAccountNumber(),
                            transaction.getTransactionAmount(),
                            transaction.getDescription(),
                            transaction.getTransactionType(),
                            transaction.getCreatedAt(),
                            transaction.getAccountBalance()
                    )
            );
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionsResponseDTO>> getMyTransactions() {
        try {
            User user = userService.getLoggedInUser();
            List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId());
            List<TransactionResponseDTO> transactionResponseDTOs = new ArrayList<>();

            for (Transaction transaction : transactions) {
                transactionResponseDTOs.add(new TransactionResponseDTO(
                        transaction.getFromAccountNumber(),
                        transaction.getToAccountNumber(),
                        transaction.getTransactionAmount(),
                        transaction.getDescription(),
                        transaction.getTransactionType(),
                        transaction.getCreatedAt(),
                        transaction.getAccountBalance()
                ));
            }

            return ApiResponse.success(
                    "Successfully fetched transactions",
                    HttpStatus.OK,
                    new TransactionsResponseDTO(transactionResponseDTOs)
            );
        }catch (Exception e){
            throw new CustomException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> withDrawToOtherAccounts(WithDrawTransactionDTO createTransactionDTO) {
        try {
            User user = userService.getLoggedInUser();
            Account fromAccount = accountRepository.findAccountByAccountNumber(createTransactionDTO.getFromAccountNumber());
            Account toAccount = accountRepository.findAccountByAccountNumber(createTransactionDTO.getToAccountNumber());

            if (fromAccount == null)
                throw new NotFoundException("From account not found");

            if (toAccount == null)
                throw new NotFoundException("To account not found");

            if (!user.getId().equals(fromAccount.getUser().getId()))
                throw new BadRequestException("You can not withdraw from account which is not yours");

            if (fromAccount.getAccountBalance() < createTransactionDTO.getTransactionAmount())
                throw new BadRequestException("Insufficient balance");

            // Sender transaction
            CreateTransactionDTO createTransactionDTO1 = new CreateTransactionDTO();
            createTransactionDTO1.setTransactionAmount(createTransactionDTO.getTransactionAmount());
            createTransactionDTO1.setDescription(createTransactionDTO.getDescription());
            createTransactionDTO1.setFromAccountNumber(createTransactionDTO.getFromAccountNumber());
            createTransactionDTO1.setToAccountNumber(createTransactionDTO.getToAccountNumber());
            createTransactionDTO1.setTransactionType(ETransactionType.WITHDRAW);
            createTransactionDTO1.setAccountBalance(fromAccount.getAccountBalance() - createTransactionDTO.getTransactionAmount());

            Transaction transaction = createTransactionEntity(fromAccount, createTransactionDTO1);

            fromAccount.setAccountBalance(fromAccount.getAccountBalance() - createTransactionDTO.getTransactionAmount());

            accountRepository.save(fromAccount);
            transaction.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
            transactionRepository.save(transaction);

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("amount", transaction.getTransactionAmount());
            context.setVariable("fromAccountName", fromAccount.getAccountName());
            context.setVariable("fromAccountNumber", transaction.getFromAccountNumber());
            context.setVariable("toAccountNumber", transaction.getToAccountNumber());
            context.setVariable("balance", transaction.getAccountBalance());

            String content = templateEngine.process("withdraw-transaction-email.html", context);
            mailService.sendEmail(user.getEmail(), "Withdraw Successful", content, true);

            // Receiver transaction
            CreateTransactionDTO createTransactionDTO2 = new CreateTransactionDTO();
            createTransactionDTO2.setTransactionAmount(createTransactionDTO.getTransactionAmount());
            createTransactionDTO2.setDescription(createTransactionDTO.getDescription());
            createTransactionDTO2.setFromAccountNumber(createTransactionDTO.getFromAccountNumber());
            createTransactionDTO2.setToAccountNumber(createTransactionDTO.getToAccountNumber());
            createTransactionDTO2.setTransactionType(ETransactionType.DEPOSIT);
            createTransactionDTO2.setAccountBalance(toAccount.getAccountBalance() + createTransactionDTO.getTransactionAmount());

            Transaction transaction2 = createTransactionEntity(toAccount, createTransactionDTO2);

            toAccount.setAccountBalance(toAccount.getAccountBalance() + createTransactionDTO.getTransactionAmount());
            accountRepository.save(toAccount);

            transaction2.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
            transactionRepository.save(transaction2);

            Context context2 = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("amount", transaction.getTransactionAmount());
            context.setVariable("accountNumber", transaction.getToAccountNumber());
            context.setVariable("accountName", toAccount.getAccountName());
            context.setVariable("balance", transaction.getAccountBalance());

            String content2 = templateEngine.process("deposit-transaction-email.html", context);
            mailService.sendEmail(user.getEmail(), "Deposit Successful", content, true);

            return ApiResponse.success(
                    "Successfully withdrawn to other account",
                    HttpStatus.CREATED,
                    new TransactionResponseDTO(
                            transaction.getFromAccountNumber(),
                            transaction.getToAccountNumber(),
                            transaction.getTransactionAmount(),
                            transaction.getDescription(),
                            transaction.getTransactionType(),
                            transaction.getCreatedAt(),
                            transaction.getAccountBalance()
                    )
            );
        }catch (Exception e){
            throw new CustomException(e);
        }
    }
}
