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
        receiverIdCheck(newTran);
        senderIdCheck(principal, newTran);
        balanceCheck(principal, newTran);
        recipientNotSenderIdCheck(newTran);

        Transaction tran = transactionDao.createTransaction(newTran);
        accountDao.updateReceiverBalance(tran.getReceiverId(), tran.getAmount());
        accountDao.updateSenderBalance(tran.getSenderId(), tran.getAmount());
        return true;
    }

    private void receiverIdCheck(Transaction newTran) {
        if (transactionChecker.wrongReceiverId(newTran)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Recipient ID found");
        }
    }

    private void senderIdCheck(Principal principal, Transaction newTran) {
        if (transactionChecker.wrongSenderId(principal.getName(), newTran)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Sender ID");
        }
    }

    private void balanceCheck(Principal principal, Transaction newTran) {
        if (!transactionChecker.sufficientBalance(principal.getName(), newTran)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Insufficient funds");
        }
    }

    private void recipientNotSenderIdCheck(Transaction newTran) {
        if (!transactionChecker.notMyAccount(newTran)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Can't send funds to self");
        }
    }
}
