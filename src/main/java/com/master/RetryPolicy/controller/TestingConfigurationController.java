package com.master.RetryPolicy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingConfiguration;
import com.master.RetryPolicy.service.TestingConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class TestingConfigurationController {

    @Autowired
    private TestingConfigurationService testingConfigurationService;

    @GetMapping("/configs")
    public ResponseEntity<JsonNode> getAll() throws ExecutionException, InterruptedException {
        Future<JsonNode> testingConfigurations = testingConfigurationService.getTestingConfigurations();
        JsonNode testingConfigurationsResp = testingConfigurations.get();
        return new ResponseEntity<>(testingConfigurationsResp, HttpStatus.OK);
    }

    @PostMapping("configs")
    public ResponseEntity<UUID> createConfig(@RequestBody TestingConfiguration testingConfiguration) throws ExecutionException, InterruptedException {
        Future<UUID> newTestingConfigurationId = testingConfigurationService.createTestingConfiguration(testingConfiguration);
        UUID testingConfigurationId = newTestingConfigurationId.get();
        if (testingConfigurationId != null) {
            return new ResponseEntity<>(testingConfigurationId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Boolean> deleteItem(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        Future<Boolean> deleteTestingConfiguration = testingConfigurationService.deleteTestingConfiguration(id);
        Boolean deletedProfileSuccess = deleteTestingConfiguration.get();
        if (deletedProfileSuccess) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
