package org.stempz.fanime.animeservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stempz.fanime.animeservice.dto.AnimeDto;
import org.stempz.fanime.animeservice.dto.AnimeSaveDto;
import org.stempz.fanime.animeservice.exception.StudioNotFoundException;
import org.stempz.fanime.animeservice.mapper.AnimeMapper;
import org.stempz.fanime.animeservice.model.Anime;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.repo.AnimeRepo;
import org.stempz.fanime.animeservice.repo.GenreRepo;
import org.stempz.fanime.animeservice.repo.StudioRepo;
import org.stempz.fanime.animeservice.service.AnimeService;
import org.stempz.fanime.animeservice.utils.AnimeUtils;
import org.stempz.fanime.animeservice.utils.GenreUtils;

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
