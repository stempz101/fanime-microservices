package org.stempz.fanime.animeservice.controller.secured;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.TEST_STUDIO_ID_1;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.TEST_STUDIO_NAME_1;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.getStudio1;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.getStudioDto1;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.getStudioDtoWithEmptyName;
import static org.stempz.fanime.animeservice.test.utils.StudioTestUtil.getStudioDtoWithNullName;
import static org.stempz.fanime.animeservice.test.utils.UserTestUtil.TEST_USER_JWT_1;
import static org.stempz.fanime.animeservice.test.utils.UserTestUtil.TEST_USER_JWT_2;

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
import org.stempz.fanime.animeservice.config.security.SecurityConfiguration;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.exception.StudioExistsException;
import org.stempz.fanime.animeservice.jwt.JwtService;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.service.StudioService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudioSecuredController.class)
@Import(SecurityConfiguration.class)
public class StudioSecuredControllerTest {

  @MockBean
  private StudioService studioService;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void create_UserIsAdmin_Success() throws Exception {
    // Given
    StudioDto studioDto = getStudioDto1();

    Studio expectedResult = getStudio1();

    // When
    when(studioService.create(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/secured/studios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(studioDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(TEST_STUDIO_ID_1),
            jsonPath("$.name").value(TEST_STUDIO_NAME_1)
        );
  }

  @Test
  void create_UserIsUser_Failure() throws Exception {
    // Given
    StudioDto studioDto = getStudioDto1();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/studios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(studioDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_2));

    // Then
    result
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  void create_StudioNameIsNull_Failure() throws Exception {
    // Given
    StudioDto studioDto = getStudioDtoWithNullName();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/studios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(studioDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_StudioNameIsEmpty_Failure() throws Exception {
    // Given
    StudioDto studioDto = getStudioDtoWithEmptyName();

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/secured/studios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(studioDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_StudioExists_Failure() throws Exception {
    // Given
    StudioDto studioDto = getStudioDto1();

    // When
    when(studioService.create(any())).thenThrow(new StudioExistsException(studioDto.getName()));

    ResultActions result = mockMvc.perform(post("/api/v1/secured/studios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(studioDto))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("Studio already exists by name: %s", studioDto.getName()))
        );
  }
}
