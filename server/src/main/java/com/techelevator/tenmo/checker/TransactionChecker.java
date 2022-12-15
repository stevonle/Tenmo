package com.techelevator.tenmo.checker;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class TransactionChecker implements TransactionCheckerInterface{
    //cant send money to self
    //cant send more than whats in balance
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
    public boolean notMyAccount() {
        return false;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
