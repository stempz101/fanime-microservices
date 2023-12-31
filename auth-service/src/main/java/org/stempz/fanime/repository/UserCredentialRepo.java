package org.stempz.fanime.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.stempz.fanime.model.UserCredential;

@Repository
public interface UserCredentialRepo extends CrudRepository<UserCredential, Long> {

  Optional<UserCredential> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

  @Query("""
      SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
      FROM UserCredential u
      WHERE LOWER(u._username) = LOWER(:username)
      """)
  boolean existsByUsernameIgnoreCase(String username);
}
