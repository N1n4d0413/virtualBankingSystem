package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TransferrDTO {
    int id;
    String username;
    String accountType;
    double amount;

}
