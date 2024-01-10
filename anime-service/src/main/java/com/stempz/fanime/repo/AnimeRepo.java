package com.stempz.fanime.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.stempz.fanime.model.Anime;

@Repository
public interface AnimeRepo extends MongoRepository<Anime, String> {

}
