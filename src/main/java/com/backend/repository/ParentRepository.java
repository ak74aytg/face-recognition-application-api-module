package com.backend.repository;

import com.backend.models.Parents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends MongoRepository<Parents, String> {
    Parents findByPhoneNo(Long phoneNo);
}
