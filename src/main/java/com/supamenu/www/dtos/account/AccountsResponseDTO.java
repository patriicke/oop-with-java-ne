package com.supamenu.www.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AccountsResponseDTO {
    private Set<AccountResponseDTO> accounts;
}
