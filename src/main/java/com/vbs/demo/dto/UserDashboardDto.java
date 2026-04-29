package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDashboardDto {
    String name;
    String username;
    String email;
    String accountType;
    double balance;
    String accountStatus;
    int depositCount;
    int withdrawCount;
    int transferCount;

}
