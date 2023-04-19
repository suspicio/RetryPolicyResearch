package com.master.RetryPolicy.utils;

import com.master.RetryPolicy.entity.TestingConfiguration;
import com.master.RetryPolicy.entity.TestingStates;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SingletonInstance {
    public static TestingConfiguration testingConfiguration = null;
    public static Integer adjustableRetryTimeout = 1000;
    public static TestingStates testingState = TestingStates.STOP;
    public static TestingStates lastTestingState = TestingStates.STOP;
    public static List<UUID> listOfAllIds = new ArrayList<>();
    public static Instant testingStartTime = null;

    public static @NotNull Boolean stateChanged() {
        if (testingState != lastTestingState) {
            final TestingStates prevTestingState = lastTestingState;
            lastTestingState = testingState;
            return prevTestingState == TestingStates.STOP;
        }
        return false;
    }

    public static @NotNull UUID getRandomUUID() {
        if (listOfAllIds.size() == 0)
            return UUID.randomUUID();
        return listOfAllIds.get((int) Math.floor(Math.random() * listOfAllIds.size()));
    }

    public static void registerForAdjustableRetry(Boolean isRetry) {
        if (isRetry) {
            switch (testingConfiguration.getRetryPolicyType()) {
                case "LILD":
                case "MILD":
                    adjustableRetryTimeout = Math.max(Math.min(adjustableRetryTimeout + 1000, 64000), 1000);
                    break;
                case "LIMD":
                case "MIMD":
                    adjustableRetryTimeout = Math.max(Math.min((int)(adjustableRetryTimeout * 1.2), 64000), 1000);
                    break;
            }
        } else {
            switch (testingConfiguration.getRetryPolicyType()) {
                case "LILD":
                case "MILD":
                    adjustableRetryTimeout =
                            Math.max(adjustableRetryTimeout - 1000, Math.max(1000, testingConfiguration.getBaseTimeout()));
                    break;
                case "LIMD":
                case "MIMD":
                    adjustableRetryTimeout =
                            Math.max((int)(adjustableRetryTimeout * 0.9), Math.max(1000, testingConfiguration.getBaseTimeout()));
                    break;
            }
        }
    }

    public static @NotNull Integer countDelayBasedOnRetry(int retryNumber, int standardTimeoutLimit,
                                                          int standardRequestLimit) {
        if (retryNumber >= standardRequestLimit)
            return -1;

        switch (testingConfiguration.getRetryPolicyType()) {
            case "simple":
                return 0;
            case "simple delay":
                return testingConfiguration.getBaseTimeout();
            case "cancel":
                return -1;
            case "incremental delay":
                return Math.min(testingConfiguration.getBaseTimeout() * retryNumber, standardTimeoutLimit);
            case "exponential backoff":
                return Math.min(testingConfiguration.getBaseTimeout() * (2 << retryNumber), standardTimeoutLimit);
            case "fibonacci backoff":
                int lastTimeout = 0;
                int lastTimeout2 = 1000;
                int timeout = 0;
                for (int i = 0; i < retryNumber; i++) {
                    timeout = Math.min(lastTimeout + lastTimeout2, standardTimeoutLimit);
                    lastTimeout = lastTimeout2;
                    lastTimeout2 = timeout;
                }
                return timeout;
            case "LILD":
            case "LIMD":
            case "MILD":
            case "MIMD":
                return adjustableRetryTimeout;
        }

        return -1;
    }

    public static @NotNull Integer countDelayBasedOnRetry(int retryNumber, int standardTimeoutLimit) {
        return countDelayBasedOnRetry(retryNumber, standardTimeoutLimit, 20);
    }

    public static @NotNull Integer countDelayBasedOnRetry(int retryNumber) {
        return countDelayBasedOnRetry(retryNumber, 64000);
    }
}
