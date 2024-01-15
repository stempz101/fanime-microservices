package com.stempz.fanime.repository;

import com.stempz.fanime.model.PasswordRecord;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRecordRepo extends CrudRepository<PasswordRecord, Long> {

  List<PasswordRecord> findAllByUser_Id(long userId);
}
