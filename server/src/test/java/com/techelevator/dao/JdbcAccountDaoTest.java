package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {
    private static final Account ACCOUNT_1 = new Account(2001,
            1001, BigDecimal.valueOf(550.00));
    private static final Account ACCOUNT_2 = new Account(2002,
            1002, BigDecimal.valueOf(1450.00));

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void listAccountId_returns_correct_list_size() {
        List<Integer> accountIds = sut.listAccountId();
        int actualListSize = accountIds.size();
        Assert.assertEquals(2, actualListSize);
    }

    @Test
    public void listAccountId_returns_correct_firstId() {
        List<Integer> accountIds = sut.listAccountId();
        int actualFirstId = accountIds.get(0);
        Assert.assertEquals(2001, actualFirstId);
    }

    @Test
    public void listAccountId_returns_correct_secondId() {
        List<Integer> accountIds = sut.listAccountId();
        int actualSecondId = accountIds.get(1);
        Assert.assertEquals(2002, actualSecondId);
    }

    @Test
    public void getAccountDetails_returns_bobs_info() {
        Account actualAccount = sut.getAccountDetails("bob");
        assertAccountsMatch(ACCOUNT_1, actualAccount);
    }

    @Test
    public void getAccountDetails_returns_users_info() {
        Account actualAccount = sut.getAccountDetails("user");
        assertAccountsMatch(ACCOUNT_2, actualAccount);
    }

    @Test
    public void getAccountDetails_returns_not_found() {
        Account actualAccount = sut.getAccountDetails("spongebob");
        Assert.assertNull(actualAccount);
    }

    @Test
    public void updateSenderBalance_subtracts_from_bobs_balance() {
        sut.updateSenderBalance(2001, BigDecimal.valueOf(100.00));
        BigDecimal actualBalance = sut.getAccountDetails("bob").getBalance();
        Assert.assertTrue(BigDecimal.valueOf(450.00).compareTo(actualBalance) == 0);
    }

    @Test
    public void updateSenderBalance_subtracts_from_users_balance() {
        sut.updateSenderBalance(2002, BigDecimal.valueOf(250.00));
        BigDecimal actualBalance = sut.getAccountDetails("user").getBalance();
        Assert.assertTrue(BigDecimal.valueOf(1200.00).compareTo(actualBalance) == 0);
    }

    @Test
    public void updateReceiverBalance_adds_to_bobs_balance() {
        sut.updateReceiverBalance(2001, BigDecimal.valueOf(50.50));
        BigDecimal actualBalance = sut.getAccountDetails("bob").getBalance();
        Assert.assertTrue(BigDecimal.valueOf(600.50).compareTo(actualBalance) == 0);
    }

    @Test
    public void updateReceiverBalance_adds_to_users_balance() {
        sut.updateReceiverBalance(2002, BigDecimal.valueOf(600.50));
        BigDecimal actualBalance = sut.getAccountDetails("user").getBalance();
        Assert.assertTrue(BigDecimal.valueOf(2050.50).compareTo(actualBalance) == 0);
    }

    private void assertAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertTrue(expected.getBalance().compareTo(actual.getBalance()) == 0);
    }
}