package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Crop;

@Repository
public interface CropRepository extends JpaRepository<Crop , Long>{
    
}
