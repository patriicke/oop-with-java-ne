package com.supamenu.www.models;

import com.supamenu.www.enumerations.account.EAccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigInteger;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Data
@OnDelete(action = OnDeleteAction.CASCADE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends Base{
    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName = "";

    @Column(name = "account_balance")
    private double accountBalance = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private EAccountStatus accountStatus = EAccountStatus.ACTIVE;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;
}
