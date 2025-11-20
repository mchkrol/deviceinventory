package com.michalkrol.deviceinventory;

import com.michalkrol.deviceinventory.config.DeviceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DeviceProperties.class)
public class DeviceinventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeviceinventoryApplication.class, args);
	}
}
