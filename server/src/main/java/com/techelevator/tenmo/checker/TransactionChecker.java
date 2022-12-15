package com.techelevator.tenmo.checker;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionChecker implements TransactionCheckerInterface{
    //cant send money to self
    private JdbcTemplate jdbcTemplate;

    public TransactionChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean sufficientBalance(String username, Transaction newTransaction) {
        Account account = null;
        String sql = "SELECT account_id, account.user_id, balance FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return (account.getBalance().compareTo(newTransaction.getAmount()) > -1);
    }

    @Override
    public boolean notMyAccount(Transaction newTransaction) {
        if (newTransaction.getReceiverId() != newTransaction.getSenderId()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean wrongReceiverId(Transaction transaction) {
        Account account = null;
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            account = mapRowToAccount(results);
            ids.add(account.getAccountId());
        }
        for (int eachId : ids) {
            if (transaction.getReceiverId() == eachId) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean wrongSenderId(String username, Transaction transaction) {
        Account account = null;
        String sql = "SELECT account_id, account.user_id, balance FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        if (account.getAccountId() == transaction.getSenderId()) {
            return false;
        }
        return true;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
