package org.stempz.fanime.animeservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.stempz.fanime.animeservice.model.Genre;

@Repository
public interface GenreRepo extends MongoRepository<Genre, String> {

  boolean existsByNameIgnoreCase(String name);
}
