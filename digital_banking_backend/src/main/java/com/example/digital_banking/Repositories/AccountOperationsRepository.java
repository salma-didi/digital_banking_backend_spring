package com.example.digital_banking.Repositories;

import com.example.digital_banking.Entities.AccountOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationsRepository extends JpaRepository<AccountOperations, Long> {
    List<AccountOperations> findByBankAccountId(String accountId);
    Page<AccountOperations> findByBankAccountId(String accountId, Pageable pageable);
    Page<AccountOperations> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
}
