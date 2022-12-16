package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.checker.TransactionCheckerInterface;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransactionController {
    private TransactionDao transactionDao;
    private TransactionCheckerInterface transactionChecker;
    private AccountDao accountDao;

    public TransactionController(TransactionDao transactionDao, TransactionCheckerInterface transactionChecker, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.transactionChecker = transactionChecker;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public List<Transaction> getTransactions(Principal principal) {
        return transactionDao.findAllTransactions(principal.getName());
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransactionById(@PathVariable int id, Principal principal) {
        Transaction transaction = transactionDao.findByTransactionId(id, principal.getName());
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed to this transaction");
        }
        return transaction;
    }

    @ResponseStatus(code = HttpStatus.ACCEPTED, reason = "Approved")
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean makeATransaction(@Valid @RequestBody Transaction newTran, Principal principal) {
        transactionChecker.wrongReceiverId(newTran);
        transactionChecker.wrongSenderId(principal.getName(), newTran);
        transactionChecker.sufficientBalance(principal.getName(), newTran);
        transactionChecker.notMyAccount(newTran);

        Transaction tran = transactionDao.createTransaction(newTran);
        accountDao.updateReceiverBalance(tran.getReceiverId(), tran.getAmount());
        accountDao.updateSenderBalance(tran.getSenderId(), tran.getAmount());
        return true;
    }

    @ResponseStatus(code = HttpStatus.CREATED, reason = "Pending")
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestATransaction(@Valid @RequestBody Transaction newTran, Principal principal) {
        transactionChecker.wrongReceiverId(newTran);
        transactionChecker.wrongSenderId(principal.getName(), newTran);
        transactionChecker.notMyAccount(newTran);

        return transactionDao.requestTransaction(newTran);
    }

    @RequestMapping(path = "/pending_transactions", method = RequestMethod.GET)
    public List<Transaction> listPendingTransactions(Principal principal) {
        return transactionDao.pendingTransactions(principal.getName());
    }

}
