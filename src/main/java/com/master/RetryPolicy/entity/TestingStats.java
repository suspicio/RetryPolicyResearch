package com.master.RetryPolicy.entity;

import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Optional;

public class TestingStats {
    private Integer currentRequests;
    private Integer currentRetryRequests;
    private HashMap<Integer, Integer> responseCodes;
    private HashMap<Integer, Pair<Integer, Double>> averageTimeForRequestsPerSecondByTime ;
    private HashMap<Integer, Pair<Integer, Integer>> successRateOfRequestsBySeconds;

    public TestingStats() {
        this.currentRequests = 0;
        this.currentRetryRequests = 0;
        this.responseCodes = new HashMap<>();
        this.averageTimeForRequestsPerSecondByTime = new HashMap<>();
        this.successRateOfRequestsBySeconds = new HashMap<>();
    }

    public TestingStats(Integer currentRequests,
                        Integer currentRetryRequests,
                        HashMap<Integer, Integer> responseCodes,
                        HashMap<Integer, Pair<Integer, Double>> averageTimeForRequestsPerSecondByTime,
                        HashMap<Integer, Pair<Integer, Integer>> successRateOfRequestsBySeconds) {
        this.currentRequests = currentRequests;
        this.currentRetryRequests = currentRetryRequests;
        this.responseCodes = responseCodes;
        this.averageTimeForRequestsPerSecondByTime = averageTimeForRequestsPerSecondByTime;
        this.successRateOfRequestsBySeconds = successRateOfRequestsBySeconds;
    }

    public Integer getCurrentRequests() {
        return currentRequests;
    }

    public Integer getCurrentRetryRequests() {
        return currentRetryRequests;
    }

    public HashMap<Integer, Integer> getResponseCodes() {
        return responseCodes;
    }

    public HashMap<Integer, Pair<Integer, Double>> getAverageTimeForRequestsPerSecondByTime() {
        return averageTimeForRequestsPerSecondByTime;
    }

    public HashMap<Integer, Pair<Integer, Integer>> getSuccessRateOfRequestsBySeconds() {
        return successRateOfRequestsBySeconds;
    }

    public void setCurrentRequests(Integer currentRequests) {
        this.currentRequests = currentRequests;
    }

    public void increaseCurrentRequest() {
        this.currentRequests++;
    }

    public void setCurrentRetryRequests(Integer currentRetryRequests) {
        this.currentRetryRequests = currentRetryRequests;
    }

    public void increaseCurrentRetryRequests() {
        this.currentRetryRequests++;
    }

    public void setResponseCodes(HashMap<Integer, Integer> responseCodes) {
        this.responseCodes = responseCodes;
    }

    public void addResponseCode(Integer responseCode) {
        Optional<Integer> count = Optional.ofNullable(this.responseCodes.putIfAbsent(responseCode, 0));
        this.responseCodes.put(responseCode, count.orElse(0) + 1);
    }

    public void setAverageTimeForRequestsPerSecondByTime(HashMap<Integer, Pair<Integer, Double>> averageTimeForRequestsPerSecondByTime) {
        this.averageTimeForRequestsPerSecondByTime = averageTimeForRequestsPerSecondByTime;
    }

    public void addAverageTimeForRequestsPerSecondByTime(Integer time, Double responseTime) {
        Optional<Pair<Integer, Double>> pairOfCountAndTotalTime = Optional.ofNullable(this.averageTimeForRequestsPerSecondByTime
                .putIfAbsent(time, Pair.of(1, responseTime)));
        this.averageTimeForRequestsPerSecondByTime.put(time, Pair.of(pairOfCountAndTotalTime.orElse(Pair.of(1, responseTime)).getFirst(),
                pairOfCountAndTotalTime.orElse(Pair.of(1, responseTime)).getSecond()));
    }

    public void setSuccessRateOfRequestsBySeconds(HashMap<Integer, Pair<Integer, Integer>> successRateOfRequestsBySeconds) {
        this.successRateOfRequestsBySeconds = successRateOfRequestsBySeconds;
    }

    public void addSuccessRateOfRequestsBySeconds(Integer time, Boolean isSuccess) {
        Optional<Pair<Integer, Integer>> pairOfCountAndSuccess = Optional.ofNullable(this.successRateOfRequestsBySeconds
                .putIfAbsent(time, Pair.of(1, isSuccess ? 1 : 0)));
        this.successRateOfRequestsBySeconds.put(time, Pair.of(pairOfCountAndSuccess.orElse(Pair.of(1, isSuccess ? 1 : 0)).getFirst(),
                pairOfCountAndSuccess.orElse(Pair.of(1, isSuccess ? 1 : 0)).getSecond()));
    }
}
