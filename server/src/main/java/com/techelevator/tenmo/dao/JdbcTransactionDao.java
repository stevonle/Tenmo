package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> findAllTransactions() {
        List<Integer> transactions = new ArrayList<>();
        String sql = "SELECT transaction_id, sender_id, receiver_id, amount " +
                     "FROM transaction;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Transaction transaction = mapRowToTransaction(results);
            transactions.add(transaction.getTransactionId());
        }
        return transactions;
    }

    @Override
    public Transaction findByTransactionId(int id) {
        Transaction transaction = null;
        String sql = "SELECT transaction_id, sender_id, receiver_id, amount " +
                     "FROM transaction WHERE transaction_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            transaction = mapRowToTransaction(results);
        }
        return transaction;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction anotherTransaction = null;
        String sql = "INSERT INTO transaction (sender_id, receiver_id, amount) " +
                "VALUES (?, ?, ?) RETURNING transaction_id;";
        int newTransactionId = jdbcTemplate.queryForObject(sql, Integer.class, transaction.getSenderId(), transaction.getReceiverId(), transaction.getAmount());
        sql = "SELECT transaction_id, sender_id, receiver_id, amount FROM transaction " +
                "WHERE transaction_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, newTransactionId);
        if (results.next()) {
            anotherTransaction = mapRowToTransaction(results);
        }
        return anotherTransaction;
    }

    private Transaction mapRowToTransaction(SqlRowSet rs) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setSenderId(rs.getInt("sender_id"));
        transaction.setReceiverId(rs.getInt("receiver_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        return transaction;
    }
}
