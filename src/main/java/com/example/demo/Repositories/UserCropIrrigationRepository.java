package com.example.demo.Repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Crops;
import com.example.demo.Entities.UserCropIrrigation;
import com.example.demo.Entities.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCropIrrigationRepository extends JpaRepository<UserCropIrrigation, Long> {

    List<UserCropIrrigation> findByUser(Users user);

    Optional<UserCropIrrigation> findByUserAndCrop(Users user, Crops crop);

    boolean existsByUserAndCrop(Users user, Crops crop);

    void deleteByUserAndCrop(Users user, Crops crop);
}
