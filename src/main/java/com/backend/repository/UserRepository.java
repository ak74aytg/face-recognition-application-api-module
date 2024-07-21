package com.backend.repository;

import com.backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public User findByEmail(String email);

    List<User> findByLocation(String loc);

    List<User> findByPincode(Integer pincode);
    @Query(value = "{}", fields = "{ 'pincode' : 1 }")
    List<User> findAllPincodes();
}
