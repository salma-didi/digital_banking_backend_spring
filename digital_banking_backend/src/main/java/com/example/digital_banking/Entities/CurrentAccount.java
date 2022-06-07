package com.example.digital_banking.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data @DiscriminatorValue("CA")
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccount extends BankAccount{
    private double overDraft;
}
