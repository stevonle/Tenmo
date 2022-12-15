package com.techelevator.tenmo.checker;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransactionCheckerTest extends BaseDaoTests {
    private static final Transaction NEW_TRANSACTION_1 = new Transaction(3004,
            2001, 2002, BigDecimal.valueOf(100.00));
    private static final Transaction NEW_TRANSACTION_2 = new Transaction(3004,
            2001, 2002, BigDecimal.valueOf(900.00));
    private static final Transaction NEW_TRANSACTION_3 = new Transaction(3004,
            2001, 2001, BigDecimal.valueOf(100.00));
    private static final Transaction NEW_TRANSACTION_4 = new Transaction(3004,
            2001, 2003, BigDecimal.valueOf(100.00));
    private static final Transaction NEW_TRANSACTION_5 = new Transaction(3004,
            2003, 2002, BigDecimal.valueOf(100.00));

    private TransactionChecker sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new TransactionChecker(jdbcTemplate);
    }

    @Test
    public void sufficientBalance_passes_check() {
        boolean actualValue = sut.sufficientBalance("bob", NEW_TRANSACTION_1);
        Assert.assertTrue(actualValue);
    }

    @Test
    public void sufficientBalance_fails_check() {
        boolean actualValue = sut.sufficientBalance("bob", NEW_TRANSACTION_2);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void notMyAccount_passes_check() {
        boolean actualValue = sut.notMyAccount(NEW_TRANSACTION_1);
        Assert.assertTrue(actualValue);
    }

    @Test
    public void notMyAccount_fails_check() {
        boolean actualValue = sut.notMyAccount(NEW_TRANSACTION_3);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void wrongReceiverId_passes_check() {
        boolean actualValue = sut.wrongReceiverId(NEW_TRANSACTION_1);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void wrongReceiverId_fails_check() {
        boolean actualValue = sut.wrongReceiverId(NEW_TRANSACTION_4);
        Assert.assertTrue(actualValue);
    }

    @Test
    public void wrongSenderId_passes_check() {
        boolean actualValue = sut.wrongSenderId("bob", NEW_TRANSACTION_1);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void wrongSenderId_fails_check() {
        boolean actualValue = sut.wrongSenderId("bob", NEW_TRANSACTION_1);
        Assert.assertFalse(actualValue);
    }

}