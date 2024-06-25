package com.supamenu.www.repositories;

import com.supamenu.www.models.Account;
import com.supamenu.www.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface IAccountRepository extends JpaRepository<Account, UUID> {
    Set<Account> findAccountByUser(User user);

    <Optional> Account findAccountByAccountNumber(String accountNumber);
}
