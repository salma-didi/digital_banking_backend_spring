package com.example.digital_banking.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountsDTO {
    private Long id;
    private String name;
    private String email;
    private List<BankAccountDTO> customerAccountsDTO;
}
