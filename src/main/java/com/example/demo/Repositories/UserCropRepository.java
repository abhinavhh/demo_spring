package com.example.demo.Repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Crops;
import com.example.demo.Entities.UserCrop;
import com.example.demo.Entities.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCropRepository extends JpaRepository<UserCrop, Long> {

    List<UserCrop> findByUser(Users user);

    Optional<UserCrop> findByUserAndCrop(Users user, Crops crop);

    boolean existsByUserAndCrop(Users user, Crops crop);

    void deleteByUserAndCrop(Users user, Crops crop);
}
