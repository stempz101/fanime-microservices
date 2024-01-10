package com.stempz.fanime.repo;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.stempz.fanime.model.Studio;

@Repository
public interface StudioRepo extends MongoRepository<Studio, String> {

  Optional<Studio> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);
}
