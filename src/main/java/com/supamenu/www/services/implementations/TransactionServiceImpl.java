package com.supamenu.www.services.implementations;

import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.dtos.transaction.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        transaction.setAccount(account);
        transaction.setTransactionAmount(createTransactionDTO.getTransactionAmount());
        transaction.setDescription(createTransactionDTO.getDescription());
        transaction.setTransactionStatus(ETransactionStatus.PENDING);
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
                throw new BadRequestException("You cannot deposit to an account that is not yours");
            }

            CreateTransactionDTO createTransactionDTO = buildCreateTransactionDTO(
                    depositTransactionDTO.getTransactionAmount(),
                    depositTransactionDTO.getDescription(),
                    "N/A",
                    depositTransactionDTO.getAccountNumber(),
                    ETransactionType.DEPOSIT,
                    account.getAccountBalance() + depositTransactionDTO.getTransactionAmount()
            );

            Transaction transaction = createTransactionEntity(account, createTransactionDTO);
            updateAccountBalance(account, depositTransactionDTO.getTransactionAmount());
            transaction.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
            transactionRepository.save(transaction);

            Context context = getDepositContext(transaction, account);

            sendTransactionEmail(transaction, "deposit-transaction-email.html", "Deposit Successful", context);

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
        } catch (Exception e) {
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
                throw new BadRequestException("You cannot withdraw from an account that is not yours");

            if (fromAccount.getAccountBalance() < createTransactionDTO.getTransactionAmount())
                throw new BadRequestException("Insufficient balance");

            handleTransaction(fromAccount, toAccount, createTransactionDTO);

            return ApiResponse.success(
                    "Successfully withdrawn to other account",
                    HttpStatus.CREATED,
                    new TransactionResponseDTO(
                            createTransactionDTO.getFromAccountNumber(),
                            createTransactionDTO.getToAccountNumber(),
                            createTransactionDTO.getTransactionAmount(),
                            createTransactionDTO.getDescription(),
                            ETransactionType.WITHDRAW,
                            fromAccount.getUpdatedAt(),
                            fromAccount.getAccountBalance()
                    )
            );
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<PaginatedTransactionsDTO>> getAllTransactions(Pageable pageable) {
        try {
            Page<Transaction> transactions = transactionRepository.findAll(pageable);
            List<TransactionResponseDTO> transactionResponseDTOs = new ArrayList<>();

            for (Transaction transaction : transactions) {
                TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO(
                        transaction.getFromAccountNumber(),
                        transaction.getToAccountNumber(),
                        transaction.getTransactionAmount(),
                        transaction.getDescription(),
                        transaction.getTransactionType(),
                        transaction.getCreatedAt(),
                        transaction.getAccountBalance()
                );
                transactionResponseDTOs.add(transactionResponseDTO);
            }

            // Create a Page<TransactionResponseDTO> from the list of DTOs and the original Page's pagination info
            Page<TransactionResponseDTO> transactionResponseDTOPage = new PageImpl<>(
                    transactionResponseDTOs,
                    pageable,
                    transactions.getTotalElements()
            );

            return ApiResponse.success(
                    "Successfully fetched transactions",
                    HttpStatus.OK,
                    new PaginatedTransactionsDTO(transactionResponseDTOPage)
            );
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    private void handleTransaction(Account fromAccount, Account toAccount, WithDrawTransactionDTO withDrawTransactionDTO) {
        // Sender transaction (withdraw)
        CreateTransactionDTO createTransactionDTO1 = buildCreateTransactionDTO(
                withDrawTransactionDTO.getTransactionAmount(),
                withDrawTransactionDTO.getDescription(),
                withDrawTransactionDTO.getFromAccountNumber(),
                withDrawTransactionDTO.getToAccountNumber(),
                ETransactionType.WITHDRAW,
                fromAccount.getAccountBalance() - withDrawTransactionDTO.getTransactionAmount()
        );

        Transaction transaction1 = createTransactionEntity(fromAccount, createTransactionDTO1);
        updateAccountBalance(fromAccount, -withDrawTransactionDTO.getTransactionAmount());
        transaction1.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
        transactionRepository.save(transaction1);

        Context context1 = getWithDrawContext(transaction1, toAccount);
        sendTransactionEmail(transaction1, "withdraw-transaction-email.html", "Withdraw Successful", context1);

        // Receiver transaction (deposit)
        CreateTransactionDTO createTransactionDTO2 = buildCreateTransactionDTO(
                withDrawTransactionDTO.getTransactionAmount(),
                withDrawTransactionDTO.getDescription(),
                withDrawTransactionDTO.getFromAccountNumber(),
                withDrawTransactionDTO.getToAccountNumber(),
                ETransactionType.DEPOSIT,
                toAccount.getAccountBalance() + withDrawTransactionDTO.getTransactionAmount()
        );

        Transaction transaction2 = createTransactionEntity(toAccount, createTransactionDTO2);
        updateAccountBalance(toAccount, withDrawTransactionDTO.getTransactionAmount());
        transaction2.setTransactionStatus(ETransactionStatus.SUCCESSFUL);
        transactionRepository.save(transaction2);

        Context context2 = getDepositContext(transaction2, fromAccount);

        sendTransactionEmail(transaction2, "deposit-transaction-email.html", "Deposit Successful", context2);
    }

    private static Context getDepositContext(Transaction transaction, Account senderAccount) {
        Context context = new Context();
        context.setVariable("firstName", transaction.getAccount().getUser().getFirstName());
        context.setVariable("amount", transaction.getTransactionAmount());
        context.setVariable("accountNumber", transaction.getToAccountNumber());
        context.setVariable("accountName", transaction.getAccount().getAccountName());
        context.setVariable("balance", transaction.getAccountBalance());
        context.setVariable("senderName", senderAccount.getUser().getFullName());
        context.setVariable("senderAccountNumber", transaction.getFromAccountNumber());
        return context;
    }

    private static Context getWithDrawContext(Transaction transaction, Account revieverAccount) {
        Context context = new Context();
        context.setVariable("firstName", transaction.getAccount().getUser().getFirstName());
        context.setVariable("amount", transaction.getTransactionAmount());
        context.setVariable("fromAccountName", transaction.getAccount().getAccountName());
        context.setVariable("fromAccountNumber", transaction.getFromAccountNumber());
        context.setVariable("toAccountNumber", transaction.getToAccountNumber());
        context.setVariable("balance", transaction.getAccountBalance());
        context.setVariable("receiverName", revieverAccount.getUser().getFullName());
        return context;
    }

    private CreateTransactionDTO buildCreateTransactionDTO(double amount, String description, String fromAccountNumber, String toAccountNumber, ETransactionType type, double balance) {
        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setTransactionAmount(amount);
        dto.setDescription(description);
        dto.setFromAccountNumber(fromAccountNumber);
        dto.setToAccountNumber(toAccountNumber);
        dto.setTransactionType(type);
        dto.setAccountBalance(balance);
        return dto;
    }

    // Update account balance
    private void updateAccountBalance(Account account, double amount) {
        account.setAccountBalance(account.getAccountBalance() + amount);
        accountRepository.save(account);
    }

    // Send transaction email
    private void sendTransactionEmail(Transaction transaction, String template, String subject, Context context) {
        String content = templateEngine.process(template, context);
        mailService.sendEmail(transaction.getAccount().getUser().getEmail(), subject, content, true);
    }
}
