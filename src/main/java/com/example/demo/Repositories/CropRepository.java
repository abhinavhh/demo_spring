package com.example.demo.Repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Crops;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crops, Long> {
    List<Crops> findByUserId(Long userId);
}
