package com.backend.repository;

import com.backend.models.ImageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDataRepository extends MongoRepository<ImageData, String> {
}
