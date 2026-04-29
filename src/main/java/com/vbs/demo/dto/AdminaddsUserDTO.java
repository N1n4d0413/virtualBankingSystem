package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AdminaddsUserDTO {

    String role;
    String name;
    String username;
    String email;
    String password;
    String accountType;
    String secQuestion;
    String secAnswer;
    double balance;


}
