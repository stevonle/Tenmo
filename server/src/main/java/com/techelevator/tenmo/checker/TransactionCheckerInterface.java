package com.techelevator.tenmo.checker;

import com.techelevator.tenmo.model.Transaction;

public interface TransactionCheckerInterface {
    boolean sufficientBalance(String username, Transaction newTransaction);
    boolean notMyAccount();
}
