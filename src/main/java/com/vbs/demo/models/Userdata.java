package com.vbs.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email", "accounttype"})}
)
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Userdata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false,unique = true)
    String username;
    @Column(nullable = false)
    String email;
    @Column(name ="accounttype",nullable = false)
    String accountType;
    @Column(nullable = false)
    String password;
    @Column(nullable = false)
    double balance;
    @Column(nullable = false)
    String accountstatus;
    @Column(name ="secquestion",nullable = false)
    String secQuestion;
    @Column(name ="secanswer",nullable = false)
    String secAnswer;
    @Column(nullable = false)
    int depositCount = 0;
    @Column(nullable = false)
    int withdrawCount = 0;
    @Column(nullable = false)
    int transferCount = 0;

}
