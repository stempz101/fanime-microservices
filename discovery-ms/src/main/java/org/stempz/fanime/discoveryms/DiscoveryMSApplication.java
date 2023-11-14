package org.stempz.fanime.discoveryms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryMSApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscoveryMSApplication.class, args);
  }
}