package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generatedAccountId = pkeyResultSet.getInt(1);
                account.setAccount_id(generatedAccountId);
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account accountLogin(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int account_id = resultSet.getInt("account_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                return new Account(account_id, username, password);
            }
        } catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<Account> getAllAccounts() {
        Connection connection = ConnectionUtil.getConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            String sql = "SELECT * FROM account";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Account account = new Account(resultSet.getInt("account_id"), resultSet.getString("username"), resultSet.getString("password"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }
}
