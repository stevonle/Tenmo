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
            2001, 2002, BigDecimal.valueOf(400.00), "approved");
    private static final Transaction TRANSACTION_2 = new Transaction(3002,
            2002, 2001, BigDecimal.valueOf(250.00), "pending");
    private static final Transaction TRANSACTION_3 = new Transaction(3003,
            2002, 2001, BigDecimal.valueOf(100.00), "rejected");
    private static final Transaction TRANSACTION_4 = new Transaction(3004,
            2002, 2001, BigDecimal.valueOf(50.00), "approved");
    private static final Transaction TRANSACTION_5 = new Transaction(3005,
            2001, 2002, BigDecimal.valueOf(300.00), "pending");
    private static final Transaction TRANSACTION_PENDING = new Transaction(3006,
            2001, 2002, BigDecimal.valueOf(400.50), "pending");

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
        Assert.assertEquals(5, actualListSize);
    }

    @Test
    public void findAllTransactions_returns_correct_user_list_size() {
        List<Transaction> transactions = sut.findAllTransactions("user");
        int actualListSize = transactions.size();
        Assert.assertEquals(5, actualListSize);
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

        Transaction actualTransaction4 = transactions.get(3);
        assertTransactionsMatch(TRANSACTION_4, actualTransaction4);

        Transaction actualTransaction5 = transactions.get(4);
        assertTransactionsMatch(TRANSACTION_5, actualTransaction5);
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
        Transaction actualTransaction = sut.findByTransactionId(3009, "bob");
        Assert.assertNull(actualTransaction);
    }

    @Test
    public void createTransaction_returns_created_transaction() {
        Transaction transaction = new Transaction(3006, 2001, 2002, BigDecimal.valueOf(400.50), "approved");
        Transaction actualTransaction = sut.createTransaction(transaction);
        assertTransactionsMatch(transaction, actualTransaction);
    }

    @Test
    public void pendingTransactions_returns_correct_bob_list_size() {
        List<Transaction> transactions = sut.pendingTransactions("bob");
        int actualSize = transactions.size();
        Assert.assertEquals(2, actualSize);
    }

    @Test
    public void pendingTransactions_returns_correct_user_list_size() {
        List<Transaction> transactions = sut.pendingTransactions("user");
        int actualSize = transactions.size();
        Assert.assertEquals(2, actualSize);
    }

    @Test
    public void requestTransaction_returns_true_if_request_made() {
        boolean isSuccessful = sut.requestTransaction(TRANSACTION_PENDING);
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void approveTransaction_returns_true_if_approved() {
        boolean isSuccessful = sut.approveTransaction(TRANSACTION_PENDING, "bob");
        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void rejectTransaction_returns_true_if_rejected() {
        boolean isSuccessful = sut.rejectTransaction(TRANSACTION_PENDING, "bob");
        Assert.assertTrue(isSuccessful);
    }

    private void assertTransactionsMatch(Transaction expected, Transaction actual) {
        Assert.assertEquals(expected.getTransactionId(), actual.getTransactionId());
        Assert.assertEquals(expected.getSenderId(), actual.getSenderId());
        Assert.assertEquals(expected.getReceiverId(), actual.getReceiverId());
        Assert.assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }
}