package com.techelevator.tenmo.checker;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransactionCheckerTest extends BaseDaoTests {
    private static final Transaction NEW_TRANSACTION_1 = new Transaction(3006,
            2001, 2002, BigDecimal.valueOf(100.00), "pending");
    private static final Transaction NEW_TRANSACTION_2 = new Transaction(3006,
            2001, 2001, BigDecimal.valueOf(100.00), "pending");
    private static final Transaction TRANSACTION_1 = new Transaction(3002,
            2002, 2001, BigDecimal.valueOf(250.00), "pending");

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
    public void notMyAccount_passes_check() {
        boolean actualValue = sut.validIds(NEW_TRANSACTION_1);
        Assert.assertTrue(actualValue);
    }

    @Test
    public void createReceiverId_passes_check() {
        boolean actualValue = sut.createReceiverId(NEW_TRANSACTION_1);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void createSenderId_passes_check() {
        boolean actualValue = sut.createSenderId("bob", NEW_TRANSACTION_1);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void requestReceiverId_passes_check() {
        boolean actualValue = sut.requestReceiverId("bob", NEW_TRANSACTION_2);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void requestSenderId_passes_check() {
        boolean actualValue = sut.requestSenderId(NEW_TRANSACTION_2);
        Assert.assertFalse(actualValue);
    }

    @Test
    public void pendingStatus_passes_check() {
        boolean actualValue = sut.pendingStatus(TRANSACTION_1);
        Assert.assertTrue(actualValue);
    }
}