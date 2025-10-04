package com.pontual_telemetria.pontual_monitor_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.pontual_telemetria.pontual_monitor_api.config")
public class PontualMonitorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PontualMonitorApiApplication.class, args);
	}

}
