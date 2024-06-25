package com.supamenu.www.models;

import com.supamenu.www.enumerations.transaction.ETransactionStatus;
import com.supamenu.www.enumerations.transaction.ETransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends Base{
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private ETransactionType transactionType;

    @Column(name = "transaction_amount")
    private double transactionAmount;

    @Column(name = "account_balance")
    private double accountBalance;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private ETransactionStatus transactionStatus;

    @Column(name = "from_account_number", nullable = true)
    private String fromAccountNumber;

    @Column(name = "to_account_number")
    private String toAccountNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;
}
