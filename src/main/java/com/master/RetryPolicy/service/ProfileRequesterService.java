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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@EnableAsync
@Async
@Service
public class ProfileRequesterService {

    private final WebClient webClient;

    private final String apiUrl = "http://localhost:8080";

    @Autowired
    private RetryProfileRequesterService retryProfileRequesterService;

    @Autowired
    public ProfileRequesterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.create("extendedPool", 1500)).responseTimeout(Duration.ofSeconds(2)))).exchangeStrategies(ExchangeStrategies.builder()
                /*.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))*/.build()).codecs(configurer -> configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder())).build();
    }

    public void resetTestingStats() {
        retryProfileRequesterService.resetTestingStats();
    }

    public Future<TestingStats> getTestingStats() throws ExecutionException, InterruptedException {
        return new AsyncResult<>(retryProfileRequesterService.getTestingStats().get());
    }

    public void addStats(Long initialTime, Duration duration, Integer statusCode, Boolean isRetry) {
        retryProfileRequesterService.addStats(initialTime, duration, statusCode, isRetry);
    }

    public void start(int requestsPerSecond) {
        final int[] count = {0};
        while (SingletonInstance.testingState == TestingStates.START || SingletonInstance.testingState == TestingStates.PAUSE) {
            try {
                if (SingletonInstance.testingState == TestingStates.PAUSE) {
                    Thread.sleep(1000);
                    continue;
                }
                if (count[0] >= requestsPerSecond) {
                    Thread.sleep(100);
                    continue;
                }

                int operationsType = 6;

                if (SingletonInstance.testingConfiguration.getCountLimit() <= SingletonInstance.listOfAllIds.size())
                    operationsType = 4;

                final int requestType = (int) Math.floor(Math.random() * operationsType);

                switch (requestType) {
                    case 0:
                        getProfileCount();
                    case 1:
                        getProfile();
                    case 2:
                        updateProfile();
                    case 3:
                        deleteProfile();
                    case 4:
                        createProfile();
                    case 5:
                        createProfile();
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
        SingletonInstance.registerForAdjustableRetry(false);
        Instant start = Instant.now();

        Mono<Tuple2<ClientResponse, JsonNode>> responseMono = webClient.get().uri(apiUrl + "/profile/{id}", SingletonInstance.getRandomUUID()).header("is-retry", "false").accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
            Mono<JsonNode> bodyMono = response.bodyToMono(JsonNode.class);
            return bodyMono.map(body -> Tuples.of(response, body));
        }).onErrorResume(error -> {

            if (error.getMessage().contains("ReadTimeoutException")) {
                System.out.println("Timeout happened");
                Duration duration = Duration.between(start, Instant.now());
                addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 504, false);
                SetTimeout.setTimeout(() -> retryProfileRequesterService.getRetryProfile(1), SingletonInstance.countDelayBasedOnRetry(1));
                return Mono.error(error);
            }
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 200, false);
            return Mono.error(error);
        });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, statusCode.value(), false);
        });
    }

    public void createProfile() {
        SingletonInstance.registerForAdjustableRetry(false);
        Instant start = Instant.now();

        Mono<Tuple2<ClientResponse, UUID>> responseMono = webClient.post().uri(apiUrl + "/profile").header("is-retry", "false").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).bodyValue(ProfileGenerator.generateRandomProfile().toJSON()).accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
            Mono<UUID> bodyMono = response.bodyToMono(UUID.class);
            return bodyMono.map(body -> Tuples.of(response, body));
        }).onErrorResume(error -> {

            if (error.getMessage().contains("ReadTimeoutException")) {
                System.out.println("Timeout happened");
                Duration duration = Duration.between(start, Instant.now());
                addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 504, false);
                SetTimeout.setTimeout(() -> retryProfileRequesterService.createRetryProfile(1), SingletonInstance.countDelayBasedOnRetry(1));
                return Mono.error(error);
            }
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 200, false);

            return Mono.error(error);
        });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            UUID uuid = tuple.getT2();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, statusCode.value(), false);
            SingletonInstance.listOfAllIds.add(uuid);
        });
    }

    public void updateProfile() {
        SingletonInstance.registerForAdjustableRetry(false);
        Instant start = Instant.now();

        UUID randomUUID = UUID.randomUUID();

        Mono<Tuple2<ClientResponse, Boolean>> responseMono = webClient.put().uri(apiUrl + "/profile").header("is-retry", "false").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).bodyValue(ProfileGenerator.generateRandomProfileWithID(randomUUID).toJSON()).accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
            Mono<Boolean> bodyMono = response.bodyToMono(Boolean.class);
            return bodyMono.map(body -> Tuples.of(response, body));
        }).onErrorResume(error -> {

            if (error.getMessage().contains("ReadTimeoutException")) {
                System.out.println("Timeout happened");
                Duration duration = Duration.between(start, Instant.now());
                addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 504, false);
                SetTimeout.setTimeout(() -> retryProfileRequesterService.updateRetryProfile(1, randomUUID), SingletonInstance.countDelayBasedOnRetry(1));
                return Mono.error(error);
            }
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 200, false);
            return Mono.error(error);
        });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, statusCode.value(), false);
        });
    }

    public void deleteProfile() {
        SingletonInstance.registerForAdjustableRetry(false);
        Instant start = Instant.now();

        UUID randomUUID = SingletonInstance.getRandomUUID();

        Mono<Tuple2<ClientResponse, Boolean>> responseMono = webClient.delete().uri(apiUrl + "/profile/{id}", randomUUID).header("is-retry", "false").accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
            Mono<Boolean> bodyMono = response.bodyToMono(Boolean.class);
            return bodyMono.map(body -> Tuples.of(response, body));
        }).onErrorResume(error -> {

            if (error.getMessage().contains("ReadTimeoutException")) {
                System.out.println("Timeout happened");
                Duration duration = Duration.between(start, Instant.now());
                addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 504, false);
                SetTimeout.setTimeout(() -> retryProfileRequesterService.deleteRetryProfile(1, randomUUID), SingletonInstance.countDelayBasedOnRetry(1));
                return Mono.error(error);
            }
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 200, false);

            return Mono.error(error);
        });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, statusCode.value(), false);
            SingletonInstance.listOfAllIds.remove(randomUUID);
        });
    }

    public void getProfileCount() {
        SingletonInstance.registerForAdjustableRetry(false);
        Instant start = Instant.now();

        Mono<Tuple2<ClientResponse, Long>> responseMono = webClient.get().uri(apiUrl + "/profile/count").header("is-retry", "false").accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
            Mono<Long> bodyMono = response.bodyToMono(Long.class);
            return bodyMono.map(body -> Tuples.of(response, body));
        }).onErrorResume(error -> {

            if (error.getMessage().contains("ReadTimeoutException")) {
                System.out.println("Timeout happened");
                Duration duration = Duration.between(start, Instant.now());
                addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 504, false);
                SetTimeout.setTimeout(() -> retryProfileRequesterService.getRetryProfileCount(1), SingletonInstance.countDelayBasedOnRetry(1));
                return Mono.error(error);
            }
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, 200, false);

            return Mono.error(error);
        });

        responseMono.subscribe(tuple -> {
            ClientResponse response = tuple.getT1();
            HttpStatus statusCode = response.statusCode();
            Duration duration = Duration.between(start, Instant.now());
            addStats(Duration.between(SingletonInstance.testingStartTime, start).toSeconds(), duration, statusCode.value(), false);
        });
    }
}