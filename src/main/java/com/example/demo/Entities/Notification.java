package com.example.demo.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Add user association for user-specific notifications
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, Users user) {
        this.message = message;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
