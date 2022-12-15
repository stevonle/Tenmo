package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    List<Integer> listAccountId();
    boolean updateSenderBalance(int senderId, BigDecimal amount);
    boolean updateReceiverBalance(int receiverId, BigDecimal amount);
    Account getAccountDetails(String username);
}
