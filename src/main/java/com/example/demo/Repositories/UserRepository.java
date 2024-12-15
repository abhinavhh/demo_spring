package com.example.demo.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);

    static List<SensorData> getSensorDatas() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSensorDatas'");
    }
    
}
