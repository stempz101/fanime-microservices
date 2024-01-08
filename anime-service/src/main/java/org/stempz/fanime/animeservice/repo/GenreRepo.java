package org.stempz.fanime.animeservice.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.stempz.fanime.animeservice.model.Genre;

@Repository
public interface GenreRepo extends MongoRepository<Genre, String> {

  @Query("{ name: { $in: ?0 } }")
  List<Genre> findAllByNameIn(List<String> names);

  boolean existsByNameIgnoreCase(String name);
}
