package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        if (isMessageValid(message)) {
            return messageDAO.insertMessage(message);
        } else {
            return null;
        }
    }

    private boolean isMessageValid(Message message) {
        return message != null &&
                message.getPosted_by() > 0 &&
                message.getMessage_text() != null &&
                !message.getMessage_text().trim().isEmpty() &&
                message.getMessage_text().length() <= 254;
    }

    public Message deleteMessage(int message_id) {
        Message message = messageDAO.getMessageById(message_id);
        if (message == null) {
            return null;
        }
        return messageDAO.deleteMessage(message_id);

    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageDAO.getMessagesByAccountId(accountId);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public Message updateTextMessage(Message existingMessage) {
        if (isMessageValid(existingMessage)) {
            boolean success = messageDAO.updateTextMessage(existingMessage).equals(existingMessage);
            if (success) {
                Message updatedMessage = messageDAO.getMessageById(existingMessage.getMessage_id());
                existingMessage.setMessage_text(updatedMessage.getMessage_text());
                return existingMessage;
            }
        }
        return null;
    }
}
