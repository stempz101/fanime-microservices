package com.stempz.fanime.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.exception.StudioNotFoundException;
import com.stempz.fanime.mapper.AnimeMapper;
import com.stempz.fanime.model.Anime;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.model.Studio;
import com.stempz.fanime.repo.AnimeRepo;
import com.stempz.fanime.repo.GenreRepo;
import com.stempz.fanime.repo.StudioRepo;
import com.stempz.fanime.service.AnimeService;
import com.stempz.fanime.utils.AnimeUtils;
import com.stempz.fanime.utils.GenreUtils;

@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

  private final AnimeRepo animeRepo;
  private final StudioRepo studioRepo;
  private final GenreRepo genreRepo;
  private final AnimeMapper animeMapper;

  @Override
  public AnimeDto create(AnimeSaveDto animeSaveDto) {
    List<Genre> genres = findSelectedGenres(animeSaveDto.getGenres());
    Studio studio = studioRepo.findByNameIgnoreCase(animeSaveDto.getStudio())
        .orElseThrow(() -> new StudioNotFoundException(animeSaveDto.getStudio()));

    Anime anime = animeMapper.toAnime(animeSaveDto);
    anime.setGenres(genres);
    anime.setStudio(studio);

    AnimeUtils.setStatusIfNull(anime);
    AnimeUtils.setSeason(anime);
    AnimeUtils.setSeasonYearIfNull(anime);

    anime = animeRepo.save(anime);

    return animeMapper.toAnimeDto(anime);
  }

  private List<Genre> findSelectedGenres(List<String> genreNames) {
    List<Genre> genres = genreRepo.findAllByNameIn(genreNames);

    GenreUtils.checkAndThrowIfGenresNotFound(genres, genreNames);

    return genres;
  }
}
