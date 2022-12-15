package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> listAccountId() {
        List<Integer> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account.getAccountId());
        }
        return accounts;
    }

    @Override
    public BigDecimal findBalanceByAccountId(String username) {
        String sql = "SELECT balance FROM account " +
                     "JOIN tenmo_user ON account.user_id = tenmo_user.user_id " +
                     "WHERE username LIKE ?;";
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, username);
        return balance;
    }

    @Override
    public boolean updateSenderBalance(int senderId, BigDecimal amount) {
        String sql = "UPDATE account SET balance = (balance - ?) " +
                     "WHERE account_id = ?;";
        return jdbcTemplate.update(sql, amount, senderId) == 1;
    }

    @Override
    public boolean updateReceiverBalance(int receiverId, BigDecimal amount) {
        String sql = "UPDATE account SET balance = (balance + ?) " +
                     "WHERE account_id = ?;";
        return jdbcTemplate.update(sql, amount, receiverId) == 1;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
