package com.vbs.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Admindata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false,unique = true)
    String username;
    @Column(nullable = false,unique = true)
    String email;
    @Column(nullable = false)
    String password;
    @Column(name ="secquestion",nullable = false)
    String secQuestion;
    @Column(name ="secanswer",nullable = false)
    String secAnswer;
}