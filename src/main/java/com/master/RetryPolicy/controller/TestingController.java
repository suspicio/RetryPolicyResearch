package com.master.RetryPolicy.controller;

import com.master.RetryPolicy.entity.TestingStates;
import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingController {
    @PostMapping("/testing/start")
    public ResponseEntity<Void> startTesting() {
        if (SingletonInstance.testingState == TestingStates.START || SingletonInstance.testingState == TestingStates.PAUSE) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (SingletonInstance.testingConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        }
        SingletonInstance.testingState = TestingStates.START;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/testing/stop")
    public ResponseEntity<Void> stopTesting() {
        SingletonInstance.testingState = TestingStates.STOP;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/testing/pause")
    public ResponseEntity<Void> pauseTesting() {
        if (SingletonInstance.testingState == TestingStates.STOP) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        SingletonInstance.testingState = TestingStates.PAUSE;
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
