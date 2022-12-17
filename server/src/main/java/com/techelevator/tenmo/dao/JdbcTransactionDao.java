package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
    public List<Transaction> findAllTransactions(String username) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT transaction_id, sender_id, receiver_id, amount, status " +
                     "FROM transaction " +
                     "JOIN account ON account.account_id = transaction.sender_id " +
                     "OR account.account_id = transaction.receiver_id " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE username ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()) {
            Transaction transaction = mapRowToTransaction(results);
            transactions.add(transaction);
        }
        return transactions;
    }

    @Override
    public Transaction findByTransactionId(int id, String username) {
        Transaction transaction = null;
        String sql = "SELECT transaction_id, sender_id, receiver_id, amount, status " +
                     "FROM transaction " +
                     "JOIN account ON transaction.sender_id = account.account_id " +
                     "OR transaction.receiver_id = account.account_id " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE transaction_id = ? AND username ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, username);
        if (results.next()) {
            transaction = mapRowToTransaction(results);
        }
        return transaction;
    }

    @Override
    public List<Transaction> pendingTransactions(String username) {
        List<Transaction> pendingTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id, sender_id, receiver_id, amount, status " +
                     "FROM transaction " +
                     "JOIN account ON account.account_id = transaction.sender_id " +
                     "OR account.account_id = transaction.receiver_id " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE username ILIKE ? AND status ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, "pending");
        while (results.next()) {
            Transaction transaction = mapRowToTransaction(results);
            pendingTransactions.add(transaction);
        }
        return pendingTransactions;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction createdTransaction = null;
        String sql = "INSERT INTO transaction (sender_id, receiver_id, amount, status) " +
                     "VALUES (?, ?, ?, ?) RETURNING transaction_id;";
        int newTransactionId = jdbcTemplate.queryForObject(sql, Integer.class,
                transaction.getSenderId(), transaction.getReceiverId(), transaction.getAmount(), "approved");
        sql = "SELECT transaction_id, sender_id, receiver_id, amount, status FROM transaction " +
                "WHERE transaction_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, newTransactionId);
        if (results.next()) {
            createdTransaction = mapRowToTransaction(results);
        }
        return createdTransaction;
    }

    @Override
    public boolean requestTransaction(Transaction tran) {
        String sql = "INSERT INTO transaction (sender_id, receiver_id, amount, status) " +
                     "VALUES (?, ?, ?, ?);";
        try {
            jdbcTemplate.update(sql, tran.getSenderId(), tran.getReceiverId(), tran.getAmount(), "pending");
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean approveTransaction(Transaction tran, String username) {
        Transaction returnedTran = null;
        String sql = "UPDATE transaction SET status = ? FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE transaction.sender_id = account.account_id " +
                     "AND username ILIKE ? AND transaction_id = ?;";
        try {
            jdbcTemplate.update(sql, "approved", username, tran.getTransactionId());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean rejectTransaction(Transaction tran, String username) {
        String sql = "UPDATE transaction SET status = ? FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE transaction.sender_id = account.account_id " +
                     "AND username ILIKE ? AND transaction_id = ?;";
        try {
            jdbcTemplate.update(sql, "rejected", username, tran.getTransactionId());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    private Transaction mapRowToTransaction(SqlRowSet rs) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setSenderId(rs.getInt("sender_id"));
        transaction.setReceiverId(rs.getInt("receiver_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setStatus(rs.getString("status"));
        return transaction;
    }
}
