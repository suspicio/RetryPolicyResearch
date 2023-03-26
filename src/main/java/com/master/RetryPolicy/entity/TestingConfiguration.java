package com.master.RetryPolicy.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table
public class TestingConfiguration {

    @PrimaryKey
    private final UUID id;

    private final Integer requestsPerSecond;

    private final String retryPolicyType;

    private final Integer countLimit;

    private final Integer baseTimeout;

    @PersistenceCreator
    public TestingConfiguration(UUID id, Integer requestsPerSecond, String retryPolicyType, Integer countLimit, Integer baseTimeout) {
        this.id = id;
        this.requestsPerSecond = requestsPerSecond;
        this.retryPolicyType = retryPolicyType;
        this.countLimit = countLimit;
        this.baseTimeout = baseTimeout;
    }

    public UUID getId() {
        return id;
    }

    public Integer getrequestsPerSecond() {
        return requestsPerSecond;
    }

    public String getRetryPolicyType() {
        return retryPolicyType;
    }
    public Integer getCountLimit() {
        return countLimit;
    }

    public Integer getBaseTimeout() {
        return baseTimeout;
    }

    @JsonCreator
    public TestingConfiguration(
            @JsonProperty("id") String id,
            @JsonProperty("request-per-second") String requestsPerSecond,
            @JsonProperty("retry-policy-type") String retryPolicyType,
            @JsonProperty("count-limit") String countLimit,
            @JsonProperty("base-timeout") String baseTimeout
    ) {
        if (Objects.equals(id, "")) {
            this.id = UUID.randomUUID();
        }
        else {
            this.id = UUID.fromString(id);
        }
        this.requestsPerSecond = Integer.parseInt(requestsPerSecond);
        this.retryPolicyType = retryPolicyType;
        this.countLimit = Integer.parseInt(countLimit);
        this.baseTimeout = Integer.parseInt(baseTimeout);
    }

    public String toString() {
        return "TestingConfiguration {\n" +
                "ID='" + id + "',\n" +
                "requestsPerSecond='" + requestsPerSecond + "',\n" +
                "retryPolicyType='" + retryPolicyType + "',\n" +
                "countLimit='" + countLimit + "',\n" +
                "baseTimeout='" + baseTimeout + "',\n" +
                "}\n";
    }

    public JsonNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", id.toString());
        json.put("request-per-second", requestsPerSecond);
        json.put("retry-policy-type", retryPolicyType);
        json.put("count-limit", countLimit);
        json.put("base-timeout", baseTimeout);
        return json;
    }
}
