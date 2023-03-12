package com.master.RetryPolicy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.utils.ProfileGenerator;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
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
public class RetryProfileRequesterService {
    private final WebClient webClient;

    private final String apiUrl = "http://localhost:8080";

    @Autowired
    public RetryProfileRequesterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.clientConnector(
                new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(20)))
        )
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder()))
                .build();
    }

    public void getRetryProfile() {
        webClient.get()
                .uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID())
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .subscribe(profileTableResp -> {
                    // Handle response
                    System.out.println("Received response: " + profileTableResp.toString());
                });
    }

    public void createRetryProfile() {
        webClient.post()
                .uri(apiUrl + "/profile")
                .header("is-retry", "true")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfile())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UUID.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }

    public void updateRetryProfile() {
        webClient.put()
                .uri(apiUrl + "/profile")
                .header("is-retry", "true")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfileWithID(SingletonInstance.getRandomUUID()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }

    public void deleteRetryProfile() {
        webClient.delete()
                .uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID())
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }

    public void getRetryProfileCount() {
        webClient.get()
                .uri(apiUrl + "/profile/count")
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Long.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }
}
