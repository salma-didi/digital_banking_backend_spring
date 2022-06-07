package com.example.digital_banking.Web;

import com.example.digital_banking.DTOs.*;
import com.example.digital_banking.Exceptions.BalanceNotSufficientException;
import com.example.digital_banking.Exceptions.BankAccountNotFoundException;
import com.example.digital_banking.Exceptions.CustomerNotFound;
import com.example.digital_banking.Service.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }


    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name="keyword" ,defaultValue ="")String keyword){
        return bankAccountService.searchCustomers(keyword+"%");
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFound {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }

    @GetMapping("/customers/{customerId}/accounts")
    public CustomerAccountsDTO getCustomerAccounts(@PathVariable Long customerId) throws CustomerNotFound {
        return bankAccountService.getCustomerAccounts(customerId);
    }

    @PostMapping("/customers/{customerId}/CurrentAccount")
    public CurrentBankAccountDTO saveCurrentAccount(@RequestBody CurrentBankAccountDTO bankAccountDTO,@PathVariable Long customerId) throws CustomerNotFound {
        this.bankAccountService.saveCurrentAccount(bankAccountDTO.getBalance(),bankAccountDTO.getOverDraft(),customerId);
        return bankAccountDTO;
    }

    @PostMapping("/customers/{customerId}/SavingAccount")
    public SavingBankAccountDTO saveSavingAccount(@RequestBody SavingBankAccountDTO bankAccountDTO,@PathVariable Long customerId) throws CustomerNotFound {
        this.bankAccountService.saveSavingAccount(bankAccountDTO.getBalance(),bankAccountDTO.getInterestRate(),customerId);
        return bankAccountDTO;
    }
}
