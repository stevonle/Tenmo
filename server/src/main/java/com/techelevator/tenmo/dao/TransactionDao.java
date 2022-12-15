package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {
    List<Transaction> findAllTransactions(String username);
    Transaction createTransaction(Transaction transaction);
    Transaction findByTransactionId(int id, String username);
}
