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

//    public Account accountLogin(Account account) {
//        return accountDAO.accountLogin(account);
//    }

    public Account accountLogin(Account account) {
        Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());
        if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
            return existingAccount;
        } else {
            return null;
        }
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

    public Account getAccountById(int postedBy) {
        return accountDAO.getAccountById(postedBy);
    }

    public boolean updateMessageText(int messageId, String messageText) {
        return accountDAO.updateMessageText(messageId, messageText);
    }
}