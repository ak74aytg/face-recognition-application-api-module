package com.backend.repository;

import com.backend.models.ImageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDataRepository extends MongoRepository<ImageData, String> {
    @Query("{ 'imageUrl': { $regex: ?0, $options: 'i' } }")
    List<ImageData> findByImageUrlContains(String regex);
}
