package org.stempz.fanime.animeservice.controller.open;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.stempz.fanime.animeservice.test.utils.GenreTestUtil.getGenre1;
import static org.stempz.fanime.animeservice.test.utils.GenreTestUtil.getGenre2;
import static org.stempz.fanime.animeservice.test.utils.GenreTestUtil.getGenre3;
import static org.stempz.fanime.animeservice.test.utils.GenreTestUtil.getGenreList;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.stempz.fanime.animeservice.config.security.SecurityConfiguration;
import org.stempz.fanime.animeservice.jwt.JwtService;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.service.GenreService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = GenreController.class)
@Import(SecurityConfiguration.class)
public class GenreControllerTest {

  @MockBean
  private GenreService genreService;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getAll_Success() throws Exception {
    // Given
    List<Genre> expectedResult = getGenreList();

    // When
    when(genreService.getAll()).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/genres")
        .contentType(MediaType.APPLICATION_JSON));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(expectedResult.size())),
            jsonPath("$[0].id").value(getGenre1().getId()),
            jsonPath("$[0].name").value(getGenre1().getName()),
            jsonPath("$[1].id").value(getGenre2().getId()),
            jsonPath("$[1].name").value(getGenre2().getName()),
            jsonPath("$[2].id").value(getGenre3().getId()),
            jsonPath("$[2].name").value(getGenre3().getName())
        );
  }
}
