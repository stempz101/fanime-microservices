package com.stempz.fanime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "message-topic")
public class AuthServiceApplicationTests {

  @Test
  public void contextLoads() {

  }
}
