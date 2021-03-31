package com.protsenko.jetbrains.test.builds_monitor_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuildsMonitorCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildsMonitorCoreApplication.class, args);
	}

}
