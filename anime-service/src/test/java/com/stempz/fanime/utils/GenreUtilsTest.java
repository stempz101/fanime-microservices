package com.stempz.fanime.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenre1;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenre3;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenreList;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenreNameList;

import java.util.List;
import org.junit.jupiter.api.Test;
import com.stempz.fanime.exception.GenreNotFoundException;
import com.stempz.fanime.model.Genre;

public class GenreUtilsTest {

  @Test
  void checkAndThrowIfGenresNotFound_Success() {
    // Given
    List<Genre> genres = getGenreList();
    List<String> genreNames = getGenreNameList();

    // When && Then
    assertDoesNotThrow(() -> GenreUtils.checkAndThrowIfGenresNotFound(genres, genreNames));
  }

  @Test
  void checkAndThrowIfGenresNotFound_ListOfGenresIsEmpty_Failure() {
    // Given
    List<Genre> genres = List.of();
    List<String> genreNames = getGenreNameList();

    // When && Then
    assertThrows(GenreNotFoundException.class,
        () -> GenreUtils.checkAndThrowIfGenresNotFound(genres, genreNames));
  }

  @Test
  void checkAndThrowIfGenresNotFound_ListOfGenresIsEmptyAndListOfGenreNamesHasOneItem_Failure() {
    // Given
    List<Genre> genres = List.of();
    List<String> genreNames = List.of(getGenre1().getName());

    // When && Then
    assertThrows(GenreNotFoundException.class,
        () -> GenreUtils.checkAndThrowIfGenresNotFound(genres, genreNames));
  }

  @Test
  void checkAndThrowIfGenresNotFound_ListOfGenresSizeIsLessThanListOfGenreNamesSize_Failure() {
    // Given
    List<Genre> genres = List.of(getGenre1(), getGenre3());
    List<String> genreNames = getGenreNameList();

    // When && Then
    assertThrows(GenreNotFoundException.class,
        () -> GenreUtils.checkAndThrowIfGenresNotFound(genres, genreNames));
  }
}
