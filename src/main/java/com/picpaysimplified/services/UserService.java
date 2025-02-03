package com.picpaysimplified.services;


import com.picpaysimplified.domain.user.User;
import com.picpaysimplified.domain.user.UserType;
import com.picpaysimplified.dtos.UserDTO;
import com.picpaysimplified.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount){
        if(sender.getUserType().equals(UserType.MERCHANT))
            throw new RuntimeException("Usuário lojista não pode realizar transações");
        if (sender.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Saldo insuficiente");
    }

    public User findUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));}

    public User createUser(UserDTO data){
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }

    public void saveUser(User user){ //Persist the user in the database
        userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }


}
