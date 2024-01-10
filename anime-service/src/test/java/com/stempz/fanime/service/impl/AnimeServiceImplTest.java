package com.stempz.fanime.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.stempz.fanime.test.utils.AnimeTestUtil.TEST_ANIME_GENRES_1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.TEST_ANIME_STUDIO_1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnime1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnimeDto1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnimeFromAnimeSaveDto1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnimeSaveDto1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.exception.GenreNotFoundException;
import com.stempz.fanime.exception.StudioNotFoundException;
import com.stempz.fanime.mapper.AnimeMapper;
import com.stempz.fanime.model.Anime;
import com.stempz.fanime.repo.AnimeRepo;
import com.stempz.fanime.repo.GenreRepo;
import com.stempz.fanime.repo.StudioRepo;
import com.stempz.fanime.test.utils.GenreTestUtil;

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
