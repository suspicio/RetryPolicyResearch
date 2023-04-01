package com.master.RetryPolicy.service;

import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.entity.TestingStats;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class TestingControlService {
    @Autowired
    private ProfileRequesterService profileRequesterService;

    public void testingController() {
        if (SingletonInstance.stateChanged() && SingletonInstance.testingState == TestingStates.START) {
            profileRequesterService.start(SingletonInstance.testingConfiguration.getRequestsPerSecond());
        }
    }

    public Future<TestingStats> getTestingStats() throws ExecutionException, InterruptedException {
        return profileRequesterService.getTestingStats();
    }

    public void resetTestingStats() {
        profileRequesterService.resetTestingStats();
    }
}
