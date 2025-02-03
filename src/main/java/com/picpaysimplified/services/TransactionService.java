package com.picpaysimplified.services;

import com.picpaysimplified.dtos.TransactionDTO;
import com.picpaysimplified.domain.transaction.Transaction;
import com.picpaysimplified.domain.user.User;
import com.picpaysimplified.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.amount());
        boolean isAuthorized = authorizeTransaction(sender, transaction.amount());
        if(!isAuthorized) throw new Exception("Transação não autorizada");

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.amount());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.amount()));
        receiver.setBalance(receiver.getBalance().add(transaction.amount()));

        this.transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
        this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver, "Você recebeu uma transação no valor de " + transaction.amount());

        return newTransaction;
    }

    public boolean authorizeTransaction(User sender,  BigDecimal amount) {
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
        if(authorizationResponse.getStatusCode() == HttpStatus.OK ){
            String message = (String)authorizationResponse.getBody().get("status");
            return "Success".equalsIgnoreCase(message);}
        else return false;

    }
}
