package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ConfigurationService {
	@Bean
	@Scope(scopeName = "prototype")
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
