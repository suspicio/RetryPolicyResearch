package com.master.RetryPolicy.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.TestingConfiguration;
import com.master.RetryPolicy.repository.TestingConfigurationRepository;
import com.master.RetryPolicy.utils.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@EnableAsync
@Async
@Service
public class TestingConfigurationService {

    @Autowired
    private TestingConfigurationRepository testingConfigurationRepository;

    public Future<UUID> createTestingConfiguration(TestingConfiguration testingConfiguration) {
        try {
            testingConfigurationRepository.save(testingConfiguration);
            return new AsyncResult<>(testingConfiguration.getId());
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Future<TestingConfiguration> selectTestingConfiguration(UUID id) {
        try {
            Optional<TestingConfiguration> testingConfiguration = testingConfigurationRepository.findById(id);
            return testingConfiguration.<Future<TestingConfiguration>>map(AsyncResult::new).orElseGet(() -> new AsyncResult<>(null));
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Future<JsonNode> getTestingConfigurations() {
        Iterable<TestingConfiguration> resp = null;
        final List<JsonNode> jsonNodeList = new ArrayList<>();
        JsonNode respJson = null;
        try {
            resp = testingConfigurationRepository.findAll();
            Consumer<TestingConfiguration> collectConfigs = testingConfiguration -> jsonNodeList.add(testingConfiguration.toJSON());
            resp.forEach(collectConfigs);
            respJson = JsonParser.fromListOfJsonToJson(jsonNodeList);
        } catch (Exception e) {
            System.err.println(e);
        }

        if (resp != null) {
            return new AsyncResult<>(respJson);
        }

        return new AsyncResult<>(null);
    }

    public Future<Boolean> deleteTestingConfiguration(UUID id) {
        boolean resp = true;
        try {
            testingConfigurationRepository.deleteById(id);
        } catch (Exception e) {
            resp = false;
            System.err.println(e);
        }
        return new AsyncResult<>(resp);
    }
}
