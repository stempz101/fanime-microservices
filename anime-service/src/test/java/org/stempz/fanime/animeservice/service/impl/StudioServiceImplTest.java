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
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio1;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio2;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudio3;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudioDto1;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudioList;
import static org.stempz.fanime.animeservice.utils.StudioTestUtil.getStudioWithoutId1;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.exception.StudioExistsException;
import org.stempz.fanime.animeservice.mapper.StudioMapper;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.repo.StudioRepo;

@ExtendWith(MockitoExtension.class)
public class StudioServiceImplTest {

  @InjectMocks
  private StudioServiceImpl studioService;

  @Mock
  private StudioRepo studioRepo;

  @Mock
  private StudioMapper studioMapper;

  @Test
  void getAll_Success() {
    // Given
    List<Studio> expectedResult = getStudioList();

    // When
    when(studioRepo.findAll()).thenReturn(expectedResult);

    List<Studio> result = studioService.getAll();

    // Then
    verify(studioRepo, times(1)).findAll();

    assertThat(result, hasSize(expectedResult.size()));
    assertThat(result, hasItems(
        getStudio1(),
        getStudio2(),
        getStudio3()
    ));
  }

  @Test
  void create_Success() {
    // Given
    StudioDto studioDto = getStudioDto1();
    Studio mappedStudio = getStudioWithoutId1();

    Studio expectedResult = getStudio1();

    // When
    when(studioRepo.existsByNameIgnoreCase(any())).thenReturn(false);
    when(studioMapper.toStudio(any())).thenReturn(mappedStudio);
    when(studioRepo.save(any())).thenReturn(expectedResult);

    Studio result = studioService.create(studioDto);

    // Then
    verify(studioRepo, times(1)).existsByNameIgnoreCase(any());
    verify(studioMapper, times(1)).toStudio(any());
    verify(studioRepo, times(1)).save(any());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void create_StudioExistsByName_Failure() {
    // Given
    StudioDto studioDto = getStudioDto1();

    // When
    when(studioRepo.existsByNameIgnoreCase(any())).thenReturn(true);

    // Then
    assertThrows(StudioExistsException.class, () -> studioService.create(studioDto));
  }
}
