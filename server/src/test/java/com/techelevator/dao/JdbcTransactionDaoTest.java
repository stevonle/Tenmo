package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private static final Transaction TRANSACTION_1 = new Transaction(3001,
            2001, 2002, BigDecimal.valueOf(500.00));
    private static final Transaction TRANSACTION_2 = new Transaction(3002,
            2002, 2001, BigDecimal.valueOf(250.00));
    private static final Transaction TRANSACTION_3 = new Transaction(3003,
            2002, 2001, BigDecimal.valueOf(100.00));

    private JdbcTransactionDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);
    }

    @Test
    public void findAllTransactions_returns_correct_bob_list_size() {
        List<Transaction> transactions = sut.findAllTransactions("bob");
        int actualListSize = transactions.size();
        Assert.assertEquals(3, actualListSize);
    }

    @Test
    public void findAllTransactions_returns_correct_user_list_size() {
        List<Transaction> transactions = sut.findAllTransactions("user");
        int actualListSize = transactions.size();
        Assert.assertEquals(3, actualListSize);
    }

    @Test
    public void findAllTransactions_returns_correct_bob_transactions() {
        List<Transaction> transactions = sut.findAllTransactions("bob");

        Transaction actualTransaction1 = transactions.get(0);
        assertTransactionsMatch(TRANSACTION_1, actualTransaction1);

        Transaction actualTransaction2 = transactions.get(1);
        assertTransactionsMatch(TRANSACTION_2, actualTransaction2);

        Transaction actualTransaction3 = transactions.get(2);
        assertTransactionsMatch(TRANSACTION_3, actualTransaction3);
    }

    @Test
    public void findTransactionId_returns_correct_transaction_for_3001() {
        Transaction actualTransaction = sut.findByTransactionId(3001, "bob");
        assertTransactionsMatch(TRANSACTION_1, actualTransaction);
    }

    @Test
    public void findTransactionId_returns_correct_transaction_for_3002() {
        Transaction actualTransaction = sut.findByTransactionId(3002, "bob");
        assertTransactionsMatch(TRANSACTION_2, actualTransaction);
    }

    @Test
    public void findTransactionId_returns_not_found() {
        Transaction actualTransaction = sut.findByTransactionId(3004, "bob");
        Assert.assertNull(actualTransaction);
    }

    @Test
    public void createTransaction_returns_created_transaction() {
        Transaction transaction = new Transaction(3004, 2001, 2002, BigDecimal.valueOf(400.50));
        Transaction actualTransaction = sut.createTransaction(transaction);
        assertTransactionsMatch(transaction, actualTransaction);
    }

    private void assertTransactionsMatch(Transaction expected, Transaction actual) {
        Assert.assertEquals(expected.getTransactionId(), actual.getTransactionId());
        Assert.assertEquals(expected.getSenderId(), actual.getSenderId());
        Assert.assertEquals(expected.getReceiverId(), actual.getReceiverId());
        Assert.assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
    }
}