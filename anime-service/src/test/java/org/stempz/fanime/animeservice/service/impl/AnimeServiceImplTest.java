package org.stempz.fanime.animeservice.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.TEST_ANIME_GENRES_1;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.TEST_ANIME_STUDIO_1;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.getAnime1;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.getAnimeDto1;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.getAnimeFromAnimeSaveDto1;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.getAnimeSaveDto1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stempz.fanime.animeservice.dto.AnimeDto;
import org.stempz.fanime.animeservice.dto.AnimeSaveDto;
import org.stempz.fanime.animeservice.exception.GenreNotFoundException;
import org.stempz.fanime.animeservice.exception.StudioNotFoundException;
import org.stempz.fanime.animeservice.mapper.AnimeMapper;
import org.stempz.fanime.animeservice.model.Anime;
import org.stempz.fanime.animeservice.repo.AnimeRepo;
import org.stempz.fanime.animeservice.repo.GenreRepo;
import org.stempz.fanime.animeservice.repo.StudioRepo;
import org.stempz.fanime.animeservice.test.utils.GenreTestUtil;

@ExtendWith(MockitoExtension.class)
public class AnimeServiceImplTest {

  @InjectMocks
  private AnimeServiceImpl animeService;

  @Mock
  private AnimeRepo animeRepo;

  @Mock
  private StudioRepo studioRepo;

  @Mock
  private GenreRepo genreRepo;

  @Mock
  private AnimeMapper animeMapper;

  @Test
  void create_Success() {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    Anime mappedAnime = getAnimeFromAnimeSaveDto1();
    Anime createdAnime = getAnime1();

    AnimeDto expectedResult = getAnimeDto1();

    // When
    when(genreRepo.findAllByNameIn(any())).thenReturn(TEST_ANIME_GENRES_1);
    when(studioRepo.findByNameIgnoreCase(any())).thenReturn(Optional.of(TEST_ANIME_STUDIO_1));
    when(animeMapper.toAnime(any(AnimeSaveDto.class))).thenReturn(mappedAnime);
    when(animeRepo.save(any())).thenReturn(createdAnime);
    when(animeMapper.toAnimeDto(any())).thenReturn(expectedResult);

    AnimeDto result = animeService.create(animeSaveDto);

    // Then
    verify(genreRepo, times(1)).findAllByNameIn(any());
    verify(studioRepo, times(1)).findByNameIgnoreCase(any());
    verify(animeMapper, times(1)).toAnime(any(AnimeSaveDto.class));
    verify(animeRepo, times(1)).save(any());
    verify(animeMapper, times(1)).toAnimeDto(any());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void create_GenreNotFoundIfListOfGenresIsEmpty_Failure() {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(genreRepo.findAllByNameIn(any())).thenReturn(new ArrayList<>());

    // Then
    assertThrows(GenreNotFoundException.class, () -> animeService.create(animeSaveDto));
  }

  @Test
  void create_GenreNotFoundIfListOfGenresIsEmptyAndListOfGenreNamesHasOneItem_Failure() {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setGenres(List.of(GenreTestUtil.getGenre1().getName()));

    // When
    when(genreRepo.findAllByNameIn(any())).thenReturn(new ArrayList<>());

    // Then
    assertThrows(GenreNotFoundException.class, () -> animeService.create(animeSaveDto));
  }

  @Test
  void create_GenreNotFoundIfListOfGenresSizeIsLessThanListOfGenreNamesSize_Failure() {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(genreRepo.findAllByNameIn(any())).thenReturn(List.of(GenreTestUtil.getGenre1()));

    // Then
    assertThrows(GenreNotFoundException.class, () -> animeService.create(animeSaveDto));
  }

  @Test
  void create_StudioNotFound_Failure() {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(genreRepo.findAllByNameIn(any())).thenReturn(TEST_ANIME_GENRES_1);
    when(studioRepo.findByNameIgnoreCase(any())).thenReturn(Optional.empty());

    // Then
    assertThrows(StudioNotFoundException.class, () -> animeService.create(animeSaveDto));
  }
}
