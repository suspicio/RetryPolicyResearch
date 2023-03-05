package com.master.RetryPolicy.service;

import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TestingScheduler {
    @Autowired
    private ProfileRequesterService profileRequesterService;

    @Scheduled(fixedDelay = 1000)
    public void testingController() {
        if (SingletonInstance.stateChanged() && SingletonInstance.testingState == TestingStates.START) {
            profileRequesterService.start(SingletonInstance.testingConfiguration.getRequestPerSecond());
        }
    }
}
