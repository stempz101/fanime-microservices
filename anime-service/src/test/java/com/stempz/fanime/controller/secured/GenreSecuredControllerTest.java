package com.stempz.fanime.controller.secured;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.stempz.fanime.test.utils.GenreTestUtil.TEST_GENRE_ID_1;
import static com.stempz.fanime.test.utils.GenreTestUtil.TEST_GENRE_NAME_1;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenre1;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenreDto1;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenreDtoWithEmptyName;
import static com.stempz.fanime.test.utils.GenreTestUtil.getGenreDtoWithNullName;
import static com.stempz.fanime.test.utils.UserTestUtil.TEST_USER_JWT_1;
import static com.stempz.fanime.test.utils.UserTestUtil.TEST_USER_JWT_2;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.stempz.fanime.dto.GenreDto;
import com.stempz.fanime.exception.GenreExistsException;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.service.GenreService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GenreSecuredController.class)
@Import(SecurityConfiguration.class)
public class GenreSecuredControllerTest {

  @MockBean
  private GenreService genreService;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void create_UserIsAdmin_Success() throws Exception {
    // Given
    GenreDto genreDto = getGenreDto1();

    Genre expectedResult = getGenre1();

    // When
    when(genreService.create(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/secured/genres")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(genreDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(TEST_GENRE_ID_1),
            jsonPath("$.name").value(TEST_GENRE_NAME_1)
        );
  }

  @Test
  void create_UserIsUser_Failure() throws Exception {
    // Given
    GenreDto genreDto = getGenreDto1();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/genres")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(genreDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_2));

    // Then
    result
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  void create_GenreNameIsNull_Failure() throws Exception {
    // Given
    GenreDto genreDto = getGenreDtoWithNullName();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/genres")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(genreDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_GenreNameIsEmpty_Failure() throws Exception {
    // Given
    GenreDto genreDto = getGenreDtoWithEmptyName();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/genres")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(genreDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_GenreExists_Failure() throws Exception {
    // Given
    GenreDto genreDto = getGenreDto1();

    // When
    when(genreService.create(any())).thenThrow(new GenreExistsException(genreDto.getName()));

    ResultActions result = mockMvc.perform(post("/api/v1/secured/genres")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(genreDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Genre already exists by name: %s", genreDto.getName()))
        );
  }
}
