package com.master.RetryPolicy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingStats;
import com.master.RetryPolicy.utils.ProfileGenerator;
import com.master.RetryPolicy.utils.SetTimeout;
import com.master.RetryPolicy.utils.SingletonInstance;
import io.netty.handler.timeout.ReadTimeoutException;
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
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Future;

@EnableAsync
@Async
@Service
public class RetryProfileRequesterService {
    private final WebClient webClient;

    private final String apiUrl = "http://localhost:8080";

    private final TestingStats testingStats;

    @Autowired
    public RetryProfileRequesterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.create("extendedPool", 1500)).responseTimeout(Duration.ofSeconds(2)))
                )
                .exchangeStrategies(ExchangeStrategies.builder()
                        /*.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))*/.build())
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder()))
                .build();
        this.testingStats = new TestingStats();
    }

    public void resetTestingStats() {
        testingStats.clear();
    }

    public Future<TestingStats> getTestingStats() {
        return new AsyncResult<>(testingStats);
    }

    public void addStats(Long initialTime, Duration duration, Integer statusCode, Boolean isRetry) {
        testingStats.addResponseCode(statusCode);
        testingStats.addSuccessRateOfRequestsBySeconds(initialTime.intValue(), statusCode < 300);
        testingStats.addAverageTimeForRequestsPerSecondByTime(initialTime.intValue(), Long.valueOf(duration.toMillis()).doubleValue());
        System.out.println("isRetry: " + isRetry);
        if (isRetry) {
            testingStats.increaseCurrentRetryRequests();
        } else {
            testingStats.increaseCurrentRequest();
        }
    }

    public void getRetryProfile() {
        Instant start = Instant.now();

        System.err.println("Retry happened");

        Mono<Tuple2<ClientResponse, JsonNode>> responseMono = webClient.get()
                .uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID())
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<JsonNode> bodyMono = response.bodyToMono(JsonNode.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("ReadTimeoutException")) {
                        System.out.println("Timeout happened");
                        Duration duration = Duration.between(start, Instant.now());
                        addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                                duration, 504, true);
                        SetTimeout.setTimeout(this::getRetryProfile, 1000);
                        return Mono.error(error);
                    }
                    Duration duration = Duration.between(start, Instant.now());
                    addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                            duration, 200, true);
                    return Mono.error(error);
                });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                    duration, statusCode.value(), true);
        });
    }

    public void createRetryProfile() {
        Instant start = Instant.now();
        System.err.println("Retry happened");

        Mono<Tuple2<ClientResponse, UUID>> responseMono = webClient.post()
                .uri(apiUrl + "/profile")
                .header("is-retry", "true")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfile().toJSON())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<UUID> bodyMono = response.bodyToMono(UUID.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("ReadTimeoutException")) {
                        System.out.println("Timeout happened");
                        Duration duration = Duration.between(start, Instant.now());
                        addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                                duration, 504, true);
                        SetTimeout.setTimeout(this::createRetryProfile, 1000);
                        return Mono.error(error);
                    }
                    Duration duration = Duration.between(start, Instant.now());
                    addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                            duration, 200, true);
                    return Mono.error(error);
                });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            UUID uuid = tuple.getT2();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                    duration, statusCode.value(), true);
            SingletonInstance.listOfAllIds.add(uuid);
        });
    }

    public void updateRetryProfile(UUID randomUUID) {
        Instant start = Instant.now();
        System.err.println("Retry happened");

        Mono<Tuple2<ClientResponse, Boolean>> responseMono = webClient.put()
                .uri(apiUrl + "/profile")
                .header("is-retry", "true")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ProfileGenerator.generateRandomProfileWithID(randomUUID).toJSON())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<Boolean> bodyMono = response.bodyToMono(Boolean.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("ReadTimeoutException")) {
                        System.out.println("Timeout happened");
                        Duration duration = Duration.between(start, Instant.now());
                        addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                                duration, 504, true);
                        SetTimeout.setTimeout(() -> updateRetryProfile(randomUUID), 1000);
                        return Mono.error(error);
                    }
                    Duration duration = Duration.between(start, Instant.now());
                    addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                            duration, 200, true);
                    return Mono.error(error);
                });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                    duration, statusCode.value(), true);
        });
    }

    public void deleteRetryProfile(UUID randomUUID) {
        Instant start = Instant.now();
        System.err.println("Retry happened");

        Mono<Tuple2<ClientResponse, Boolean>> responseMono = webClient.delete()
                .uri(apiUrl + "/profile/{id}", randomUUID)
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<Boolean> bodyMono = response.bodyToMono(Boolean.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("ReadTimeoutException")) {
                        System.out.println("Timeout happened");
                        Duration duration = Duration.between(start, Instant.now());
                        addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                                duration, 504, true);
                        SetTimeout.setTimeout(() -> deleteRetryProfile(randomUUID), 1000);
                        return Mono.error(error);
                    }
                    Duration duration = Duration.between(start, Instant.now());
                    addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                            duration, 200, true);
                    return Mono.error(error);
                });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                    duration, statusCode.value(), true);
            SingletonInstance.listOfAllIds.remove(randomUUID);
        });
    }

    public void getRetryProfileCount() {
        Instant start = Instant.now();
        System.err.println("Retry happened");

        Mono<Tuple2<ClientResponse, Long>> responseMono = webClient.get()
                .uri(apiUrl + "/profile/count")
                .header("is-retry", "true")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    Mono<Long> bodyMono = response.bodyToMono(Long.class);
                    return bodyMono.map(body -> Tuples.of(response, body));
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("ReadTimeoutException")) {
                        System.out.println("Timeout happened");
                        Duration duration = Duration.between(start, Instant.now());
                        addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                                duration, 504, true);
                        SetTimeout.setTimeout(this::getRetryProfileCount, 1000);
                        return Mono.error(error);
                    }
                    Duration duration = Duration.between(start, Instant.now());
                    addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                            duration, 200, true);
                    return Mono.error(error);
                });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(),
                    duration, statusCode.value(), true);
        });
    }
}
