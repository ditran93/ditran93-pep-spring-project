package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.InvalidMessageException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Optional<Message> createMessage(Message message) {
        Optional<Account> optionalAccount = accountRepository.findById(message.getPostedBy());
        if(message.getMessageText() == null || message.getMessageText().isBlank()) throw new InvalidMessageException("Message text cannot be null or blank");
        if(message.getMessageText().length() > 255) throw new InvalidMessageException("Message text cannot exceed 255 characters");
        if(!optionalAccount.isPresent()) throw new InvalidMessageException("PostedBy account does not exist");

        return Optional.of(messageRepository.save(message));
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(int messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if(optionalMessage.isPresent()) return optionalMessage;
        return Optional.empty();
    }

    public Integer deleteMessageById(int messageId) {
        if(messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1;
        }
        return 0;
    }

    public Integer updateMessage(int messageId, String newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if(!optionalMessage.isPresent()) {
            return 0;
        }

        if(newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) return 0;
        Message message = optionalMessage.get();
        message.setMessageText(newMessageText);
        messageRepository.save(message);
        return 1;
    }

    public List<Message> getAllMessagesByAccountId(int accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}
