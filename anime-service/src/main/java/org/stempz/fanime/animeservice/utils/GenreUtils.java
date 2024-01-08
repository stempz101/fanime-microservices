package org.stempz.fanime.animeservice.utils;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.stempz.fanime.animeservice.exception.GenreNotFoundException;
import org.stempz.fanime.animeservice.model.Genre;

@UtilityClass
public class GenreUtils {

  public void checkAndThrowIfGenresNotFound(List<Genre> genres, List<String> names) {
    if (genres.isEmpty()) {
      if (names.size() == 1) {
        throw new GenreNotFoundException(names.get(0));
      }
      throw new GenreNotFoundException(names);
    } else if (genres.size() < names.size()) {
      List<String> notFoundGenres = names.stream()
          .filter(name -> genres.stream().noneMatch(genre -> genre.getName().equals(name)))
          .toList();

      throw new GenreNotFoundException(notFoundGenres);
    }
  }
}
