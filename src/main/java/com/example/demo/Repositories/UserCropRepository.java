package com.example.demo.Repositories;


import com.example.demo.Entities.UserCrops;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




import java.util.List;
import java.util.Optional;

@Repository

public interface UserCropRepository extends JpaRepository<UserCrops, Long> {
    List<UserCrops> findByUserId(Long userId);
    Optional<UserCrops> findByUserIdAndCropId(Long userId, Long cropId);


}
