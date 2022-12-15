package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.checker.TransactionChecker;
import com.techelevator.tenmo.checker.TransactionCheckerInterface;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private UserDao userDao;
    private TransactionCheckerInterface transactionChecker;

    public TenmoController(AccountDao accountDao, TransactionDao transactionDao, UserDao userDao, TransactionCheckerInterface transactionChecker) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.userDao = userDao;
        this.transactionChecker = transactionChecker;
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Integer> accounts() {
        return accountDao.listAccountId();
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        return accountDao.findBalanceByAccountId(principal.getName());
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public List<Integer> getTransactions() {
        return transactionDao.findAllTransactions();
    }

    @RequestMapping(path = "/transaction/{id}", method = RequestMethod.GET)
    public Transaction getTransactionById(@PathVariable int id) {
        return transactionDao.findByTransactionId(id);
    }

    @ResponseStatus(code = HttpStatus.ACCEPTED, reason = "Approved")
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean makeATransaction(@Valid @RequestBody Transaction newTransaction, Principal principal) {
        if (transactionChecker.sufficientBalance(principal.getName(), newTransaction)) {
            Transaction transaction = transactionDao.createTransaction(newTransaction);
            accountDao.updateReceiverBalance(transaction.getReceiverId(), transaction.getAmount());
            accountDao.updateSenderBalance(transaction.getSenderId(), transaction.getAmount());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Insufficient funds");
        }
        return true;
    }
}
