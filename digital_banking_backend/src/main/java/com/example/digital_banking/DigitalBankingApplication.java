package com.example.digital_banking;

import com.example.digital_banking.DTOs.BankAccountDTO;
import com.example.digital_banking.DTOs.CurrentBankAccountDTO;
import com.example.digital_banking.DTOs.CustomerDTO;
import com.example.digital_banking.DTOs.SavingBankAccountDTO;
import com.example.digital_banking.ENUMS.AccountStatus;
import com.example.digital_banking.ENUMS.OperationType;
import com.example.digital_banking.Entities.*;
import com.example.digital_banking.Exceptions.BalanceNotSufficientException;
import com.example.digital_banking.Exceptions.BankAccountNotFoundException;
import com.example.digital_banking.Exceptions.CustomerNotFound;
import com.example.digital_banking.Repositories.AccountOperationsRepository;
import com.example.digital_banking.Repositories.BankAccountRepository;
import com.example.digital_banking.Repositories.CustomerRepository;
import com.example.digital_banking.Service.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }

   @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Salma","Soukaina", "Ihsan").forEach(name->{
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    for(int i=0;i<2;i++){
                        bankAccountService.saveCurrentAccount(Math.random()*90000, 9000+i*100, customer.getId());
                        bankAccountService.saveSavingAccount(Math.random()*120000, 5.5+i*10, customer.getId());
                    }

                } catch (CustomerNotFound e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount: bankAccounts) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO){
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    }
                    else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();

                    }
                    bankAccountService.credit(accountId, 100000+Math.random()*120000, "Crédit");
                    bankAccountService.debit(accountId, 1000+Math.random()*90000, "Débit");

            }
        };
    }



}
