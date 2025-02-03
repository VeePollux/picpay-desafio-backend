package com.picpaysimplified.repositories;

import com.picpaysimplified.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //Receive the User entity and the type of the primary key
    Optional<User> findUserByDocument(String document); //Jpa will implement this method for us
    Optional<User> findUserById(Long Id); //Jpa will implement this method for us

}
