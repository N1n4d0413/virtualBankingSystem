package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsignupDto {
    String role;
    String name;
    String username;
    String email;
    String accountType;
    String password;
    String secQuestion;
    String secAnswer;
}
