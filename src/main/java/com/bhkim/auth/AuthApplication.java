package com.bhkim.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

//@ComponentScan(basePackageClasses = )
@SpringBootApplication(scanBasePackageClasses = {BasePackage.class})
@ConfigurationPropertiesScan({"com.bhkim.auth.properties"})
public class  AuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

//	@Bean
//	public AuditorAware<Long> auditorProvider() {
//		return () -> Optional.of(0L);
//	}
}
