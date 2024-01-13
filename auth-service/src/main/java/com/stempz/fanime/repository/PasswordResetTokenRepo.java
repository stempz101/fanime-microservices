package com.stempz.fanime.repository;

import com.stempz.fanime.model.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepo extends CrudRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByUserId(long userId);

  Optional<PasswordResetToken> findByToken(UUID token);

  void deleteByToken(UUID token);
}
