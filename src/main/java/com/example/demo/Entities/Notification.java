package com.example.demo.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Updated column name from "read" to "is_read" to avoid reserved keyword issues.
    @Column(name = "is_read", nullable = false)
    private Boolean isread = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public Notification() {}

    public Notification(String message, Users user) {
        this.message = message;
        this.user = user;
        this.isread = false;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isread;
    }

    public void setRead(boolean read){
        this.isread = read;
    }

    // Overridden equals and hashCode methods for proper entity comparisons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    // Overridden toString method for better logging and debugging
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                ", read=" + isread +
                ", createdAt=" + createdAt +
                '}';
    }
}
