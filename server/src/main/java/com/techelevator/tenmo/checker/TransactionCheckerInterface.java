package com.techelevator.tenmo.checker;

import com.techelevator.tenmo.model.Transaction;

public interface TransactionCheckerInterface {
    boolean sufficientBalance(String username, Transaction newTransaction);
    boolean validIds(Transaction newTransaction);
    boolean createReceiverId(Transaction transaction);
    boolean createSenderId(String username, Transaction transaction);
    boolean requestSenderId(Transaction transaction);
    boolean requestReceiverId(String username, Transaction transaction);
    boolean pendingStatus(Transaction transaction);
}
