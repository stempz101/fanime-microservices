package org.stempz.fanime.animeservice.controller.open;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio1;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio2;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio3;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudioList;

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
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.service.StudioService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudioController.class)
@Import(SecurityConfiguration.class)
public class StudioControllerTest {

  @MockBean
  private StudioService studioService;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getAll_Success() throws Exception {
    // Given
    List<Studio> expectedResult = getStudioList();

    // When
    when(studioService.getAll()).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/studios")
        .contentType(MediaType.APPLICATION_JSON));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(expectedResult.size())),
            jsonPath("$[0].id").value(getStudio1().getId()),
            jsonPath("$[0].name").value(getStudio1().getName()),
            jsonPath("$[1].id").value(getStudio2().getId()),
            jsonPath("$[1].name").value(getStudio2().getName()),
            jsonPath("$[2].id").value(getStudio3().getId()),
            jsonPath("$[2].name").value(getStudio3().getName())
        );
  }
}
