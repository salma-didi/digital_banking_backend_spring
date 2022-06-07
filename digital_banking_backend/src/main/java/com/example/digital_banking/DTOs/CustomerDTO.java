package com.example.digital_banking.DTOs;


import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}