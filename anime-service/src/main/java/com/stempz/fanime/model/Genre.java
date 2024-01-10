package com.stempz.fanime.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

  @Id
  private String id;
  @Indexed(unique = true)
  private String name;
}
