package com.example.demo.Repositories;

import com.example.demo.Entities.Crops;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crops, Long> {
    // Use nested property name since the field is "user" (not "userId")
    // List<Crops> findByUser_Id(Long userId);
}
