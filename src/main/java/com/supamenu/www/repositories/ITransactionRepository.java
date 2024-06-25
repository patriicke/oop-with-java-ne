package com.supamenu.www.repositories;

import com.supamenu.www.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
    List<Transaction> findAllByUserId(UUID userId);
}
