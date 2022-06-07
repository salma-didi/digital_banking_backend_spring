package com.example.digital_banking.Service;

import com.example.digital_banking.DTOs.*;
import com.example.digital_banking.ENUMS.OperationType;
import com.example.digital_banking.Entities.*;
import com.example.digital_banking.Exceptions.BalanceNotSufficientException;
import com.example.digital_banking.Exceptions.BankAccountNotFoundException;
import com.example.digital_banking.Exceptions.CustomerNotFound;
import com.example.digital_banking.Mappers.BankAccountMapperImpl;
import com.example.digital_banking.Repositories.AccountOperationsRepository;
import com.example.digital_banking.Repositories.BankAccountRepository;
import com.example.digital_banking.Repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j //gerer la journalisation (logger les messages, ...)

public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationsRepository accountOperationsRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentAccount(double initialBalance, double OverDraft, Long customerId) throws CustomerNotFound {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFound("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(OverDraft);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingAccount(double initialBalance, double InterestRate, Long customerId) throws CustomerNotFound {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFound("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(InterestRate);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        /*List<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer: customers) {
            CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }*/
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accoundId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accoundId).
                orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));

        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else {
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance()<amount){
            throw new BalanceNotSufficientException("Balance non suffisante");
        }
        AccountOperations accountOperations = new AccountOperations();
        accountOperations.setType(OperationType.DEBIT);
        accountOperations.setAmount(amount);
        accountOperations.setDescription(description);
        accountOperations.setOperationDate(new Date());
        accountOperations.setBankAccount(bankAccount);
        accountOperationsRepository.save(accountOperations);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperations accountOperations = new AccountOperations();
        accountOperations.setType(OperationType.CREDIT);
        accountOperations.setAmount(amount);
        accountOperations.setDescription(description);
        accountOperations.setOperationDate(new Date());
        accountOperations.setBankAccount(bankAccount);
        accountOperationsRepository.save(accountOperations);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount, "Transfer from "+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountsDTOs = bankAccounts.stream().map(bankAccount -> {

            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountsDTOs;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFound{
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFound("Customer Not Found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationsDTO> accountHistory(String accountId){
        List<AccountOperations> accountOperations = accountOperationsRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Bank Account not found");
        Page<AccountOperations> accountOperations = accountOperationsRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO =new AccountHistoryDTO();
        List<AccountOperationsDTO> accountOperationsDTOs = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationsDTOS(accountOperationsDTOs);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers=customerRepository.searchCustomers(keyword);
        List<CustomerDTO> customerDTOS =customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
        return  customerDTOS;
    }

    @Override
    public CustomerAccountsDTO getCustomerAccounts(Long CustomerId) throws CustomerNotFound {
        Customer customer=customerRepository.findById(CustomerId).orElse(null);
        if(customer==null) throw new CustomerNotFound("Customer not found");
        List<BankAccount> Customeraccounts = bankAccountRepository.findByCustomerId(CustomerId);
        CustomerAccountsDTO customerDTO =new CustomerAccountsDTO();
       // List<BankAccountDTO> CustomerAccountsDTOs = Customeraccounts.getContent().stream().map(bankAccount ->dtoMapper.fromBankAccount(bankAccount)).collect(Collectors.toList());
        List<BankAccountDTO> bankAccountsDTOs = Customeraccounts.stream().map(bankAccount -> {

            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        customerDTO.setCustomerAccountsDTO(bankAccountsDTOs);
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        return customerDTO;
    }
}
