package com.master.RetryPolicy.controller;

import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.entity.TestingStats;
import com.master.RetryPolicy.service.TestingControlService;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class TestingController {

    @Autowired
    private TestingControlService testingControlService;

    @PostMapping("/testing/start")
    public ResponseEntity<Void> startTesting() {
        if (SingletonInstance.testingState == TestingStates.START || SingletonInstance.testingState == TestingStates.PAUSE) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (SingletonInstance.testingConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        }

        testingControlService.resetTestingStats();
        SingletonInstance.testingStartTime = Instant.now();
        SingletonInstance.testingState = TestingStates.START;
        testingControlService.testingController();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/testing/stop")
    public ResponseEntity<Void> stopTesting() {
        SingletonInstance.testingState = TestingStates.STOP;
        testingControlService.testingController();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/testing/pause")
    public ResponseEntity<Void> pauseTesting() {
        if (SingletonInstance.testingState == TestingStates.STOP) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        SingletonInstance.testingState = TestingStates.PAUSE;
        testingControlService.testingController();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/testing/stats")
    public ResponseEntity<TestingStats> getTestingStats() throws ExecutionException, InterruptedException {
        Future<TestingStats> testingStatsFuture = testingControlService.getTestingStats();
        TestingStats testingStats = testingStatsFuture.get();
        return new ResponseEntity<>(testingStats, HttpStatus.OK);
    }
}
