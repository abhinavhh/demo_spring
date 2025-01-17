package com.example.demo.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public void setUser(Users user){
        this.user=user;
    }

    public void setToken(String token){
        this.token=token;
    }

    public void setExpiryDate(LocalDateTime expiryDate){
        this.expiryDate = expiryDate;
    }

    public Users getUser(){
        return user;
    }

    public String getToken(){
        return token;
    }

    public LocalDateTime getExpiryDate(){
        return expiryDate;
    }
}