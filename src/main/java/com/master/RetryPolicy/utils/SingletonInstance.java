package com.master.RetryPolicy.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingConfiguration;
import com.master.RetryPolicy.entity.TestingStates;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SingletonInstance {
    public static Integer currentRequests = 0;
    public static Integer currentOutRequests = 0;
    public static Integer currentRetryRequests = 0;
    public static Integer currentOutRetryRequests = 0;
    public static TestingConfiguration testingConfiguration = null;
    public static TestingStates testingState = TestingStates.STOP;
    public static TestingStates lastTestingState = TestingStates.STOP;
    public static List<UUID> listOfAllIds;

    public static @NotNull Boolean stateChanged() {
        if (testingState != lastTestingState) {
            final TestingStates prevTestingState = lastTestingState;
            lastTestingState = testingState;
            return prevTestingState == TestingStates.STOP;
        }
        return false;
    }
}
