package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.exception.ExistedUsernameException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> registerAccount(Account account) throws ExistedUsernameException {
        if(account.getUsername() == null || account.getUsername().isEmpty()) {
            return Optional.empty();
        }
        if(account.getPassword() == null || account.getPassword().length() < 4) {
            return Optional.empty();
        }
        if(accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new ExistedUsernameException("Username already exists");
        } 
        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> loginAccount(Account account) {
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        if(existingAccount.isPresent() && existingAccount.get().getPassword().equals(account.getPassword())) {
            return existingAccount;
        } else {
            return Optional.empty();
        }
    }
}
