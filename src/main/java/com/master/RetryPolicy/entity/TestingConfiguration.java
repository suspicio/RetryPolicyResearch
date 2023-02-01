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

    private final Integer requestPerSecond;

    private final String retryPolicyType;

    private final Integer countLimit;

    @PersistenceCreator
    public TestingConfiguration(UUID id, Integer requestPerSecond, String retryPolicyType, Integer countLimit) {
        this.id = id;
        this.requestPerSecond = requestPerSecond;
        this.retryPolicyType = retryPolicyType;
        this.countLimit = countLimit;
    }

    public UUID getId() {
        return id;
    }

    public Integer getRequestPerSecond() {
        return requestPerSecond;
    }

    public String getRetryPolicyType() {
        return retryPolicyType;
    }
    public Integer getCountLimit() {
        return countLimit;
    }

    @JsonCreator
    public TestingConfiguration(
            @JsonProperty("id") String id,
            @JsonProperty("request-per-second") String requestPerSecond,
            @JsonProperty("retry-policy-type") String retryPolicyType,
            @JsonProperty("count-limit") String countLimit
    ) {
        if (Objects.equals(id, "")) {
            this.id = UUID.randomUUID();
        }
        else {
            this.id = UUID.fromString(id);
        }
        this.requestPerSecond = Integer.parseInt(requestPerSecond);
        this.retryPolicyType = retryPolicyType;
        this.countLimit = Integer.parseInt(countLimit);
    }

    public String toString() {
        return "TestingConfiguration {\n" +
                "ID='" + id + "',\n" +
                "requestsPerSecond='" + requestPerSecond + "',\n" +
                "retryPolicyType='" + retryPolicyType + "',\n" +
                "countLimit='" + countLimit + "',\n" +
                "}\n";
    }

    public JsonNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", id.toString());
        json.put("request-per-second", requestPerSecond);
        json.put("retry-policy-type", retryPolicyType);
        json.put("count-limit", countLimit);
        return json;
    }
}
