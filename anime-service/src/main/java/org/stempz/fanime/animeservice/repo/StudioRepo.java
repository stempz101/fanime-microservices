package org.stempz.fanime.animeservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.stempz.fanime.animeservice.model.Studio;

@Repository
public interface StudioRepo extends MongoRepository<Studio, String> {

}
