package com.stempz.fanime.controller.secured;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnimeDto1;
import static com.stempz.fanime.test.utils.AnimeTestUtil.getAnimeSaveDto1;
import static com.stempz.fanime.test.utils.UserTestUtil.TEST_USER_JWT_1;
import static com.stempz.fanime.test.utils.UserTestUtil.TEST_USER_JWT_2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.stempz.fanime.config.security.SecurityConfiguration;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.exception.GenreNotFoundException;
import com.stempz.fanime.exception.StudioNotFoundException;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.model.enums.Status;
import com.stempz.fanime.service.AnimeService;
import com.stempz.fanime.validator.AnimeSaveDtoValidator;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AnimeSecuredController.class)
@Import(SecurityConfiguration.class)
public class AnimeSecuredControllerTest {

  @MockBean
  private AnimeService animeService;

  @SpyBean
  private AnimeSaveDtoValidator animeSaveDtoValidator;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void create_UserIsAdmin_Success() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    AnimeDto expectedResult = getAnimeDto1();

    // When
    when(animeService.create(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(expectedResult.getId()),
            jsonPath("$.title").value(expectedResult.getTitle()),
            jsonPath("$.status").value(expectedResult.getStatus().name()),
            jsonPath("$.description").value(expectedResult.getDescription()),
            jsonPath("$.genres").value(hasSize(expectedResult.getGenres().size())),
            jsonPath("$.genres[*].name")
                .value(containsInAnyOrder(expectedResult.getGenres().stream()
                    .map(Genre::getName)
                    .toArray())),
            jsonPath("$.studio.name").value(expectedResult.getStudio().getName()),
            jsonPath("$.startDate").value(expectedResult.getStartDate().toString()),
            jsonPath("$.endDate").value(expectedResult.getEndDate().toString()),
            jsonPath("$.season").value(expectedResult.getSeason().name()),
            jsonPath("$.seasonYear").value(expectedResult.getSeasonYear()),
            jsonPath("$.totalEpisodes").value(expectedResult.getTotalEpisodes()),
            jsonPath("$.airedEpisodes").value(expectedResult.getAiredEpisodes()),
            jsonPath("$.duration").value(expectedResult.getDuration())
        );
  }

  @Test
  void create_UserIsUser_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_2));

    // Then
    result
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  void create_TitleIsNull_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setTitle(null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Title must not be empty")
        );
  }

  @Test
  void create_TitleIsBlank_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setTitle("");

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Title must not be empty")
        );
  }

  @Test
  void create_ListOfGenresIsNull_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setGenres(null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("At least one genre must be selected")
        );
  }

  @Test
  void create_ListOfGenresIsEmpty_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setGenres(List.of());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("At least one genre must be selected")
        );
  }

  @Test
  void create_StudioIsNull_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStudio(null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Studio must be selected")
        );
  }

  @Test
  void create_StudioIsBlank_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStudio("");

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Studio must be selected")
        );
  }

  @Test
  void create_TitleAndStudioIsBlankAndListOfGenresIsNull_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setTitle("");
    animeSaveDto.setGenres(null);
    animeSaveDto.setStudio("");

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(3)),
            jsonPath("$[*].message")
                .value(containsInAnyOrder(
                    "Title must not be empty",
                    "At least one genre must be selected",
                    "Studio must be selected"
                ))
        );
  }

  @Test
  void create_StartYearIsNotEqualToSeasonYear_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStartDate(LocalDate.of(animeSaveDto.getSeasonYear() - 5, 1, 1));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Start year must be equal to the provided season year")
        );
  }

  @Test
  void create_StartYearIsBeforeSeasonYear_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStartDate(LocalDate.of(animeSaveDto.getSeasonYear() - 1, 1, 1));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Start year must be equal to the provided season year")
        );
  }

  @Test
  void create_StartYearIsAfterSeasonYear_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStartDate(LocalDate.of(animeSaveDto.getSeasonYear() + 1, 1, 1));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Start year must be equal to the provided season year")
        );
  }

  @Test
  void create_EndYearIsBeforeSeasonYear_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setEndDate(LocalDate.of(animeSaveDto.getSeasonYear() - 5, 12, 31));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("End year must be equal to or after the provided season year")
        );
  }

  @Test
  void create_EndYearIsBeforeSeasonYear_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setEndDate(LocalDate.of(animeSaveDto.getSeasonYear() - 1, 12, 31));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("End year must be equal to or after the provided season year")
        );
  }

  @Test
  void create_EndDateIsBeforeStartDate_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setEndDate(animeSaveDto.getStartDate().minusDays(30));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("End date must be equal to or after the start date")
        );
  }

  @Test
  void create_EndDateIsBeforeStartDate_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setEndDate(animeSaveDto.getStartDate().minusDays(1));

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("End date must be equal to or after the start date")
        );
  }

  @Test
  void create_TotalEpisodesIsLessThanZero_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setTotalEpisodes(-10);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Total and aired episodes cannot be negative")
        );
  }

  @Test
  void create_TotalEpisodesIsLessThanZero_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setTotalEpisodes(-1);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Total and aired episodes cannot be negative")
        );
  }

  @Test
  void create_AiredEpisodesIsLessThanZero_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setAiredEpisodes(-10);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Total and aired episodes cannot be negative")
        );
  }

  @Test
  void create_AiredEpisodesIsLessThanZero_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setAiredEpisodes(-1);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Total and aired episodes cannot be negative")
        );
  }

  @Test
  void create_AiredEpisodesExceedTotalEpisodes_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setAiredEpisodes(animeSaveDto.getTotalEpisodes() + 10);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Aired episodes cannot exceed total episodes")
        );
  }

  @Test
  void create_AiredEpisodesExceedTotalEpisodes_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setAiredEpisodes(animeSaveDto.getTotalEpisodes() + 1);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Aired episodes cannot exceed total episodes")
        );
  }

  @Test
  void create_SpecifiedAiredEpisodesWhenStartDateIsNull_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStartDate(null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Provide a start date before setting the number of aired episodes")
        );
  }

  @Test
  void create_SpecifiedAiredEpisodesBeforeRelease_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    LocalDate currentDate = LocalDate.now().plusDays(10);
    long daysBetweenStartDate = ChronoUnit.DAYS.between(animeSaveDto.getStartDate(), currentDate);
    long daysBetweenEndDate = ChronoUnit.DAYS.between(animeSaveDto.getEndDate(), currentDate);

    animeSaveDto.setStartDate(animeSaveDto.getStartDate().plusDays(daysBetweenStartDate));
    animeSaveDto.setEndDate(animeSaveDto.getEndDate().plusDays(daysBetweenEndDate));
    animeSaveDto.setSeasonYear(animeSaveDto.getStartDate().getYear());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Aired episodes cannot be set until the anime is released")
        );
  }

  @Test
  void create_SpecifiedAiredEpisodesBeforeRelease_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    LocalDate currentDate = LocalDate.now().plusDays(1);
    long daysBetweenStartDate = ChronoUnit.DAYS.between(animeSaveDto.getStartDate(), currentDate);
    long daysBetweenEndDate = ChronoUnit.DAYS.between(animeSaveDto.getEndDate(), currentDate);

    animeSaveDto.setStartDate(animeSaveDto.getStartDate().plusDays(daysBetweenStartDate));
    animeSaveDto.setEndDate(animeSaveDto.getEndDate().plusDays(daysBetweenEndDate));
    animeSaveDto.setSeasonYear(animeSaveDto.getStartDate().getYear());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Aired episodes cannot be set until the anime is released")
        );
  }

  @Test
  void create_InvalidNotYetReleasedStatus_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.NOT_YET_RELEASED.name());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'NOT YET RELEASED' should have no episodes aired")
        );
  }

  @Test
  void create_InvalidNotYetReleasedStatus_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.NOT_YET_RELEASED.name());
    animeSaveDto.setAiredEpisodes(1);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'NOT YET RELEASED' should have no episodes aired")
        );
  }

  @Test
  void create_InvalidReleasingStatus_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.RELEASING.name());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'RELEASING' should have some episodes aired, and the number of aired episodes should be between 1 and total episodes")
        );
  }

  @Test
  void create_InvalidReleasingStatusWhenAiredEpisodesIsLessThanOrEqualsZero_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.RELEASING.name());
    animeSaveDto.setAiredEpisodes(0);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'RELEASING' should have some episodes aired, and the number of aired episodes should be between 1 and total episodes")
        );
  }

  @Test
  void create_InvalidReleasingStatusWhenAiredEpisodesIsGreaterThanOrEqualsTotalEpisodes_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.RELEASING.name());
    animeSaveDto.setAiredEpisodes(animeSaveDto.getTotalEpisodes());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'RELEASING' should have some episodes aired, and the number of aired episodes should be between 1 and total episodes")
        );
  }

  @Test
  void create_InvalidFinishedStatus_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.FINISHED.name());
    animeSaveDto.setAiredEpisodes(0);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'FINISHED' must have all episodes aired")
        );
  }

  @Test
  void create_InvalidFinishedStatus_EdgeCase_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();
    animeSaveDto.setStatus(Status.FINISHED.name());
    animeSaveDto.setAiredEpisodes(animeSaveDto.getTotalEpisodes() - 1);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Anime with status 'FINISHED' must have all episodes aired")
        );
  }

  @Test
  void create_StatusIsCancelled_Success() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    AnimeDto expectedResult = getAnimeDto1();

    animeSaveDto.setStatus(Status.CANCELLED.name());
    expectedResult.setStatus(Status.CANCELLED);

    // When
    when(animeService.create(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(expectedResult.getId()),
            jsonPath("$.title").value(expectedResult.getTitle()),
            jsonPath("$.status").value(expectedResult.getStatus().name()),
            jsonPath("$.description").value(expectedResult.getDescription()),
            jsonPath("$.genres").value(hasSize(expectedResult.getGenres().size())),
            jsonPath("$.genres[*].name")
                .value(containsInAnyOrder(expectedResult.getGenres().stream()
                    .map(Genre::getName)
                    .toArray())),
            jsonPath("$.studio.name").value(expectedResult.getStudio().getName()),
            jsonPath("$.startDate").value(expectedResult.getStartDate().toString()),
            jsonPath("$.endDate").value(expectedResult.getEndDate().toString()),
            jsonPath("$.season").value(expectedResult.getSeason().name()),
            jsonPath("$.seasonYear").value(expectedResult.getSeasonYear()),
            jsonPath("$.totalEpisodes").value(expectedResult.getTotalEpisodes()),
            jsonPath("$.airedEpisodes").value(expectedResult.getAiredEpisodes()),
            jsonPath("$.duration").value(expectedResult.getDuration())
        );
  }

  @Test
  void create_InvalidStatus_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    String invalidStatus = "InvalidStatus";
    animeSaveDto.setStatus(invalidStatus);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Invalid anime status: %s", invalidStatus))
        );
  }

  @Test
  void create_GenreNotFoundWithDefaultMessage_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(animeService.create(any())).thenThrow(new GenreNotFoundException());

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Genre is not found")
        );
  }

  @Test
  void create_GenreNotFoundIfListOfSelectedGenresHasOneItem_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(animeService.create(any()))
        .thenThrow(new GenreNotFoundException(animeSaveDto.getGenres().get(0)));

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Genre with name \"%s\" is not found",
                    animeSaveDto.getGenres().get(0)))
        );
  }

  @Test
  void create_GenresNotFoundIfListOfSelectedGenresHasManyItems_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(animeService.create(any()))
        .thenThrow(new GenreNotFoundException(animeSaveDto.getGenres()));

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Genres with names %s are not found",
                    animeSaveDto.getGenres()))
        );
  }

  @Test
  void create_StudioNotFoundWithDefaultMessage_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(animeService.create(any())).thenThrow(new StudioNotFoundException());

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Studio is not found")
        );
  }

  @Test
  void create_StudioNotFoundWithSpecifiedName_Failure() throws Exception {
    // Given
    AnimeSaveDto animeSaveDto = getAnimeSaveDto1();

    // When
    when(animeService.create(any()))
        .thenThrow(new StudioNotFoundException(animeSaveDto.getStudio()));

    ResultActions result = mockMvc.perform(post("/api/v1/secured/anime")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(animeSaveDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Studio with name \"%s\" is not found",
                    animeSaveDto.getStudio()))
        );
  }
}
