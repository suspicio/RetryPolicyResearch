package com.master.RetryPolicy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.UUID;

@EnableAsync
@Async
@Service
public class ProfileRequesterService {

    private final WebClient webClient;

    private final String apiUrl = "http://localhost:8080";

    @Autowired
    public ProfileRequesterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.clientConnector(
                new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(20)))
        ).build();
    }

    public void start(int requestPerSecond) {
        while (SingletonInstance.testingState == TestingStates.START || SingletonInstance.testingState == TestingStates.PAUSE) {
            try {
                if (SingletonInstance.testingState == TestingStates.PAUSE) {
                    Thread.sleep(1000);
                    continue;
                }
                Thread.sleep(1000 / requestPerSecond);
                webClient.get()
                        .uri("http://localhost:8080/profile/{id}", UUID.randomUUID())
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .subscribe(profileTableResp -> {
                            // Handle response
                            System.out.println("Received response: " + profileTableResp.toString());
                        });
            } catch (InterruptedException e) {
                // handle exception
            }
        }
    }
}
