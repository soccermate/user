package com.example.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@SpringBootApplication
@EnableReactiveFeignClients
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);

		ConnectionProvider provider =
				ConnectionProvider.builder("custom")
						.maxConnections(50)
						.maxIdleTime(Duration.ofSeconds(20))
						.maxLifeTime(Duration.ofSeconds(60))
						.pendingAcquireTimeout(Duration.ofSeconds(60))
						.evictInBackground(Duration.ofSeconds(120))
						.build();

		HttpClient client = HttpClient.create(provider);
	}

}
