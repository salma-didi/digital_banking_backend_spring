package com.example.digital_banking.DTOs;

import com.example.digital_banking.ENUMS.OperationType;
import lombok.Data;
import java.util.Date;

@Data
public class AccountOperationsDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}