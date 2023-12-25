package org.stempz.fanime.animeservice.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stempz.fanime.animeservice.dto.GenreDto;
import org.stempz.fanime.animeservice.exception.GenreExistsException;
import org.stempz.fanime.animeservice.mapper.GenreMapper;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.repo.GenreRepo;
import org.stempz.fanime.animeservice.service.impl.GenreServiceImpl;
import org.stempz.fanime.animeservice.utils.GenreTestUtil;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

  @InjectMocks
  private GenreServiceImpl genreService;

  @Mock
  private GenreRepo genreRepo;

  @Mock
  private GenreMapper genreMapper;

  @Test
  void getAll_Success() {
    // Given
    List<Genre> expectedResult = GenreTestUtil.getGenreList();

    // When
    when(genreRepo.findAll()).thenReturn(expectedResult);

    List<Genre> result = genreService.getAll();

    // Then
    verify(genreRepo, times(1)).findAll();

    assertThat(result, hasSize(expectedResult.size()));
    assertThat(result, hasItems(
        GenreTestUtil.getGenre1(),
        GenreTestUtil.getGenre2(),
        GenreTestUtil.getGenre3()
    ));
  }

  @Test
  void create_Success() {
    // Given
    GenreDto genreDto = GenreTestUtil.getGenreDto1();
    Genre mappedGenre = GenreTestUtil.getGenreWithoutId1();

    Genre expectedResult = GenreTestUtil.getGenre1();

    // When
    when(genreRepo.existsByNameIgnoreCase(any())).thenReturn(false);
    when(genreMapper.toGenre(any())).thenReturn(mappedGenre);
    when(genreRepo.save(any())).thenReturn(expectedResult);

    Genre result = genreService.create(genreDto);

    // Then
    verify(genreRepo, times(1)).existsByNameIgnoreCase(any());
    verify(genreMapper, times(1)).toGenre(any());
    verify(genreRepo, times(1)).save(any());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void create_GenreExistsByName_Failure() {
    // Given
    GenreDto genreDto = GenreTestUtil.getGenreDto1();

    // When
    when(genreRepo.existsByNameIgnoreCase(any())).thenReturn(true);

    // Then
    assertThrows(GenreExistsException.class, () -> genreService.create(genreDto));
  }
}
