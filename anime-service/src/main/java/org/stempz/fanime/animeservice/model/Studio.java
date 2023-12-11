package org.stempz.fanime.animeservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "studios")
@Data
public class Studio {

  @Id
  private String id;
  @Indexed(unique = true)
  private String name;
}
