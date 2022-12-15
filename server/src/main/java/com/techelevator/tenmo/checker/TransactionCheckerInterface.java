package com.techelevator.tenmo.checker;

import com.techelevator.tenmo.model.Transaction;

public interface TransactionCheckerInterface {
    boolean sufficientBalance(String username, Transaction newTransaction);
    boolean notMyAccount(Transaction newTransaction);
    boolean wrongReceiverId(Transaction transaction);
    boolean wrongSenderId(String username, Transaction transaction);
}
