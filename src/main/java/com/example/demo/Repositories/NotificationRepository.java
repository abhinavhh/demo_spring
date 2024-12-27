package com.example.demo.Repositories;

import com.example.demo.Entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findByUserIdOrderByTimestampDesc(Long userId);
}
