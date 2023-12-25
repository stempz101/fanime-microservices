package org.stempz.fanime.animeservice.utils;

import java.util.List;
import java.util.stream.Stream;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.model.Studio;

public class StudioTestUtil {

  public static final String TEST_STUDIO_ID_1 = "1";
  public static final String TEST_STUDIO_NAME_1 = "A-1 Pictures";

  public static final String TEST_STUDIO_ID_2 = "2";
  public static final String TEST_STUDIO_NAME_2 = "MAPPA";

  public static final String TEST_STUDIO_ID_3 = "3";
  public static final String TEST_STUDIO_NAME_3 = "Wit Studio";

  public static Studio getStudio1() {
    return new Studio(TEST_STUDIO_ID_1, TEST_STUDIO_NAME_1);
  }

  public static Studio getStudio2() {
    return new Studio(TEST_STUDIO_ID_2, TEST_STUDIO_NAME_2);
  }

  public static Studio getStudio3() {
    return new Studio(TEST_STUDIO_ID_3, TEST_STUDIO_NAME_3);
  }

  public static Studio getStudioWithoutId1() {
    return new Studio(null, TEST_STUDIO_NAME_1);
  }

  public static StudioDto getStudioDto1() {
    return new StudioDto(TEST_STUDIO_NAME_1);
  }

  public static StudioDto getStudioDto2() {
    return new StudioDto(TEST_STUDIO_NAME_2);
  }

  public static StudioDto getStudioDto3() {
    return new StudioDto(TEST_STUDIO_NAME_3);
  }

  public static StudioDto getStudioDtoWithNullName() {
    return new StudioDto(null);
  }

  public static StudioDto getStudioDtoWithEmptyName() {
    return new StudioDto("");
  }

  public static List<Studio> getStudioList() {
    return List.of(getStudio1(), getStudio2(), getStudio3());
  }
}
