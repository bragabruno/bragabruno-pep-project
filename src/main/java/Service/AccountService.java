package Service;

import DAO.AccountDAO;
import Model.Account;

import java.util.List;

public class AccountService {

    AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public Account addAccount(Account account) {
        if (isUsernameNotBlank(account.getUsername()) && isPasswordValid(account.getPassword())) {
            return accountDAO.insertAccount(account);
        } else {
            return null; // or throw an exception indicating validation failure
        }
    }

    public Account accountLogin(Account account) {
        return accountDAO.accountLogin(account);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    private boolean isUsernameNotBlank(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        int minLength = 4; // minimum password length
        return password != null && password.length() >= minLength;
    }
}