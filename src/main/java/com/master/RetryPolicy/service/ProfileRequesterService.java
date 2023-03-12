package com.master.RetryPolicy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.entity.TestingStats;
import com.master.RetryPolicy.utils.ProfileGenerator;
import com.master.RetryPolicy.utils.SetTimeout;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@EnableAsync
@Async
@Service
public class ProfileRequesterService {

    private final WebClient webClient;

    private final String apiUrl = "http://localhost:8080";

    private final TestingStats testingStats;

    @Autowired
    private RetryProfileRequesterService retryProfileRequesterService;

    @Autowired
    public ProfileRequesterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.clientConnector(
                new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(20)))
        )
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder()))
                .build();
        this.testingStats = new TestingStats();
    }

    public Future<TestingStats> getTestingStats() {
        return new AsyncResult<>(testingStats);
    }

    public void start(int requestPerSecond) {
        final int[] count = {0};
        while (SingletonInstance.testingState == TestingStates.START || SingletonInstance.testingState == TestingStates.PAUSE) {
            try {
                if (SingletonInstance.testingState == TestingStates.PAUSE) {
                    Thread.sleep(1000);
                    continue;
                }
                if (count[0] >= requestPerSecond) {
                    Thread.sleep(100);
                    continue;
                }

                int operationsType = 5;

                if (SingletonInstance.testingConfiguration.getCountLimit() <= SingletonInstance.listOfAllIds.size())
                    operationsType = 4;

                final int requestType = (int) Math.floor(Math.random() * operationsType);

                switch (requestType) {
                    case 0: getProfileCount();
                    case 1: getProfile();
                    case 2: updateProfile();
                    case 3: deleteProfile();
                    case 4: createProfile();
                    default:
                        count[0]++;
                        SetTimeout.setTimeout(() -> count[0]--, 1000);
                        break;
                }
            } catch (InterruptedException e) {
                // handle exception
            }
        }
    }

    public void getProfile() {
        Instant start = Instant.now();

        Mono<Tuple2<ClientResponse, JsonNode>> responseMono = webClient.get()
                .uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID())
                .header("is-retry", "false")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<JsonNode> bodyMono = response.bodyToMono(JsonNode.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error instanceof TimeoutException) {
                        Duration duration = Duration.between(start, Instant.now());
                        System.out.println("API call timed out after " + duration.toMillis() + "ms");
                        retryProfileRequesterService.getRetryProfile();
                    }
                    return Mono.error(error);
                });;

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            JsonNode jsonNode = tuple.getT2();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            System.out.println("Response code: " + statusCode);
            System.out.println("Response time: " + duration.toMillis() + "ms");
            System.out.println("JsonNode: " + jsonNode.toString());
        });
    }

    public void createProfile() {
        System.out.println("Started UUID Creation");
        webClient.post()
                .uri(apiUrl + "/profile")
                .header("is-retry", "false")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfile())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UUID.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response UUID: " + profileTableResp);
                });
    }

    public void updateProfile() {
        webClient.put()
                .uri(apiUrl + "/profile")
                .header("is-retry", "false")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfileWithID(SingletonInstance.getRandomUUID()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }

    public void deleteProfile() {
        webClient.delete()
                .uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID())
                .header("is-retry", "false")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }

    public void getProfileCount() {
        webClient.get()
                .uri(apiUrl + "/profile/count")
                .header("is-retry", "false")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Long.class)
                .subscribe(profileTableResp -> {
                    System.out.println("Received response: " + profileTableResp);
                });
    }
}