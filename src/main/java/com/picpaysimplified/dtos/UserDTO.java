package com.picpaysimplified.dtos;

import com.picpaysimplified.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, String email, String document, String password, BigDecimal balance, UserType userType) {
}
