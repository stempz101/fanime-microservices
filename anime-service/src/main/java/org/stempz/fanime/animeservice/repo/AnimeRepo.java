package org.stempz.fanime.animeservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.stempz.fanime.animeservice.model.Anime;

@Repository
public interface AnimeRepo extends MongoRepository<Anime, String> {

}
