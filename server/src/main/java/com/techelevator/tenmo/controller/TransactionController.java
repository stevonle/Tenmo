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
    private TransactionCheckerInterface check;
    private AccountDao accountDao;

    public TransactionController(TransactionDao transactionDao, TransactionCheckerInterface check, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.check = check;
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

    @RequestMapping(path = "/pending", method = RequestMethod.GET)
    public List<Transaction> listPendingTransactions(Principal principal) {
        return transactionDao.pendingTransactions(principal.getName());
    }

    @ResponseStatus(code = HttpStatus.ACCEPTED, reason = "Approved")
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean makeATransaction(@Valid @RequestBody Transaction newTran, Principal principal) {
        check.createReceiverId(newTran);
        check.createSenderId(principal.getName(), newTran);
        check.validIds(newTran);
        check.sufficientBalance(principal.getName(), newTran);
        Transaction tran = transactionDao.createTransaction(newTran);
        accountDao.updateReceiverBalance(tran.getReceiverId(), tran.getAmount());
        accountDao.updateSenderBalance(tran.getSenderId(), tran.getAmount());
        return true;
    }

    @ResponseStatus(code = HttpStatus.CREATED, reason = "Pending")
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestATransaction(@Valid @RequestBody Transaction newTran, Principal principal) {
        check.requestReceiverId(principal.getName(), newTran);
        check.requestSenderId(newTran);
        check.validIds(newTran);
        return transactionDao.requestTransaction(newTran);
    }

    @RequestMapping(path = "/approve", method = RequestMethod.PUT)
    public boolean approveTransaction(@Valid @RequestBody Transaction tran, Principal principal) {
        check.createReceiverId(tran);
        check.createSenderId(principal.getName(), tran);
        check.validIds(tran);
        check.pendingStatus(tran);
        check.sufficientBalance(principal.getName(), tran);
        transactionDao.approveTransaction(tran, principal.getName());
        accountDao.updateReceiverBalance(tran.getReceiverId(), tran.getAmount());
        accountDao.updateSenderBalance(tran.getSenderId(), tran.getAmount());
        return true;
    }

    @RequestMapping(path = "/reject", method = RequestMethod.PUT)
    public boolean rejectTransaction(@Valid @RequestBody Transaction tran, Principal principal) {
        check.createReceiverId(tran);
        check.createSenderId(principal.getName(), tran);
        check.validIds(tran);
        check.pendingStatus(tran);
        transactionDao.rejectTransaction(tran, principal.getName());
        return true;
    }

}
