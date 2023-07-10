package Controller;

import Model.Account;
import Service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    private AccountService accountService;


    public Javalin startAPI() {
        Javalin app = Javalin.create();
//        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerUserHandler);
        app.get("/account", this::getAccountHandler);
//        app.start(8080);

        return app;
    }


    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
//    private void exampleHandler(Context context) {
//        context.json("sample text");
//    }
    private void getAccountHandler(Context context) {
        List<Account> accounts = accountService.getAllAccounts();
        context.json(accounts);
    }

    private void registerUserHandler(Context context) throws JsonProcessingException {
        AccountService accountService = new AccountService();
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        // Check if the username is not blank
        if (account.getUsername().isEmpty()) {
            context.status(400).result("Username cannot be blank");
            return;
        }

        // Check if the password is at least 4 characters long
        if (account.getPassword().length() < 4) {
            context.status(400).result("Password must be at least 4 characters long");
            return;
        }

        // Check if an account with the same username already exists
        List<Account> existingAccounts = accountService.getAllAccounts();
        boolean isDuplicateUsername = existingAccounts.stream()
                .anyMatch(a -> a.getUsername().equals(account.getUsername()));
        if (isDuplicateUsername) {
            context.status(400).result("An account with the same username already exists");
            return;
        }

        // Persist the account to the database
        Account createdAccount = accountService.addAccount(account);

        // If account creation was successful, return the created account
        if (createdAccount != null) {
            context.status(200).json(mapper.writeValueAsString(createdAccount));
        } else {
            context.status(400).result("Account creation failed");
        }
    }
}
