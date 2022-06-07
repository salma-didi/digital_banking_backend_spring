package com.example.digital_banking.Service;

import com.example.digital_banking.DTOs.*;
import com.example.digital_banking.Entities.BankAccount;
import com.example.digital_banking.Entities.CurrentAccount;
import com.example.digital_banking.Entities.Customer;
import com.example.digital_banking.Entities.SavingAccount;
import com.example.digital_banking.Exceptions.BalanceNotSufficientException;
import com.example.digital_banking.Exceptions.BankAccountNotFoundException;
import com.example.digital_banking.Exceptions.CustomerNotFound;

import java.util.List;

public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentBankAccountDTO saveCurrentAccount(double initialBalance, double OverDraft, Long customerId) throws CustomerNotFound;
    SavingBankAccountDTO saveSavingAccount(double initialBalance, double InterestRate, Long customerId) throws CustomerNotFound;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accoundId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFound;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationsDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);

    CustomerAccountsDTO getCustomerAccounts(Long CustomerId) throws CustomerNotFound;

}
