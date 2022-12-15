package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.checker.TransactionChecker;
import com.techelevator.tenmo.checker.TransactionCheckerInterface;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {
    //ADD METHOD TO GET ACCOUNT INFO

    private AccountDao accountDao;

    public TenmoController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/account/details", method = RequestMethod.GET)
    public Account account(Principal principal) {
        return accountDao.getAccountDetails(principal.getName());
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Integer> accounts() {
        return accountDao.listAccountId();
    }
}
