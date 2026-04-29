package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SecQverifyDTO {

    String username;
    String secQuestion;
    String answer;
}
