package com.vbs.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "userid",nullable = false)
    private int userId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private LocalDateTime date;

    @PrePersist
    protected void onCreate()
    {
        this.date = LocalDateTime.now();
    }
}
