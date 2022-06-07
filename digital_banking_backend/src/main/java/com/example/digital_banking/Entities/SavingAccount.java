package com.example.digital_banking.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@DiscriminatorValue("SA")
@Data @Entity
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount extends BankAccount{
    private double interestRate;
}
