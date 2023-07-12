package org.magnum.mobilecloud.video;


import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableResourceServer

@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses=org.magnum.mobilecloud.video.repository.VideoRepository.class)
@Configuration
@EnableWebMvc
@ComponentScan
public class Application {

	private static final int MAX_REQUEST_SIZE_IN_MB = 150;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	// This configuration element adds the ability to accept multipart
	// requests to the web container.
	
}
