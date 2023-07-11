package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
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

    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }


    public Javalin startAPI() {
        Javalin app = Javalin.create();
//        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);
        app.get("/account", this::getAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageTextHandler);
//        app.start(8080);

        return app;
    }

    private void updateMessageTextHandler(Context context) throws IOException, SQLException {
        String message_id_str = context.pathParam("message_id");
        if (message_id_str == null || !message_id_str.matches("\\d+")) {
            context.status(400).result("");
            return;
        }
        int message_id = Integer.parseInt(message_id_str);

        Message message = messageService.getMessageById(message_id);
        if (message == null) {
            context.status(400).result("");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        Message updatedMessage = mapper.readValue(context.body(), Message.class);
        if (updatedMessage.getMessage_text() == null || updatedMessage.getMessage_text().trim().isEmpty()) {
            context.status(400).result("");
            return;
        }
        if (updatedMessage.getMessage_text().length() > 254) {
            context.status(400).result("");
            return;
        }

        message.setMessage_text(updatedMessage.getMessage_text());
        messageService.updateTextMessage(message);

        String responseBody = mapper.writeValueAsString(message);
        context.status(200).json(responseBody);
    }





    private void getMessageByIdHandler(Context context) {
        String message_id_str = context.pathParam("message_id");
        if (message_id_str == null || !message_id_str.matches("\\d+")) {
            context.status(400).result("");
            return;
        }
        int message_id = Integer.parseInt(message_id_str);

        Message message = messageService.getMessageById(message_id);

        if (message == null) {
            context.status(200).result("");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        String responseBody;
        try {
            responseBody = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500).result("");
            return;
        }

        context.status(200).json(responseBody);
    }

    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        List<Message> messages = messageService.getAllMessages();

        ObjectMapper mapper = new ObjectMapper();
        String responseBody;

        if (messages.isEmpty()) {
            responseBody = mapper.writeValueAsString(Collections.emptyList());
        } else {
            responseBody = mapper.writeValueAsString(messages);
        }

        context.status(200).json(responseBody);
    }


    private void getMessagesByAccountIdHandler(Context context) throws JsonProcessingException {
        String account_id_str = context.pathParam("account_id");
        if (account_id_str == null || !account_id_str.matches("\\d+")) {
            context.status(400).result("");
            return;
        }
        int account_id = Integer.parseInt(account_id_str);

        // Get all messages posted by the account with the specified account_id
        List<Message> messages = messageService.getMessagesByAccountId(account_id);

        // Return a 400 status code with an empty response body if no messages are found
        if (messages == null || messages.isEmpty()) {
            context.status(200).result("[]");
            return;
        }

        // Return the list of messages in the response body
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(messages);
        context.status(200).json(jsonResponse);
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
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        // Check if the username is not blank
        if (account.getUsername().isBlank() || account.getUsername() == null || account.getUsername().equals("") || account.getUsername().isEmpty()) {
            context.status(400).result("");
            return;
        }

        // Check if the password is at least 4 characters long
        if (account.getPassword().length() < 4) {
            context.status(400).result("");
            return;
        }

        // Check if an account with the same username already exists
        List<Account> existingAccounts = accountService.getAllAccounts();
        boolean isDuplicateUsername = existingAccounts.stream()
                .anyMatch(a -> a.getUsername().equals(account.getUsername()));
        if (isDuplicateUsername) {
            context.status(400).result("");
            return;
        }

        // Persist the account to the database
        Account createdAccount = accountService.addAccount(account);

        // If account creation was successful, return the created account
        if (createdAccount != null) {
            context.status(200).json(mapper.writeValueAsString(createdAccount));
        } else {
            context.status(400).result("");
        }
    }

    private void loginUserHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        // Check if the account exists and the password is correct
        Account authenticatedAccount = accountService.accountLogin(account);
        if (authenticatedAccount != null) {
            context.status(200).json(mapper.writeValueAsString(authenticatedAccount));
        } else {
            context.status(401).result("");
        }
    }

    

    private void createMessageHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        // Check if the user exists
        Account account = accountService.getAccountById(message.getPosted_by());
        if (account == null) {
            context.status(400).result("");
            return;
        }

        // Add the message to the database
        Message createdMessage = messageService.addMessage(message);
        if (createdMessage != null) {
            context.status(200).json(mapper.writeValueAsString(createdMessage));
        } else {
            context.status(400).result("");
        }
    }

    private void deleteMessageHandler(Context context) throws JsonProcessingException {
        String message_id_str = context.pathParam("message_id");
        if (!message_id_str.matches("\\d+")) {
            context.status(400).result("");
            return;
        }
        int message_id = Integer.parseInt(message_id_str);

        // Delete the message from the database
        Message deletedMessage = messageService.deleteMessage(message_id);
        if (deletedMessage != null) {
            ObjectMapper mapper = new ObjectMapper();
            context.status(200).json(mapper.writeValueAsString(deletedMessage));
        } else {
            context.status(200).result("");
        }
    }
}
