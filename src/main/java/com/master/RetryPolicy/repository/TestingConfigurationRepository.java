package com.master.RetryPolicy.repository;

import com.master.RetryPolicy.entity.TestingConfiguration;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface TestingConfigurationRepository extends CassandraRepository<TestingConfiguration, UUID> {

    @Query("SELECT * FROM store.testingConfiguration ALLOW FILTERING;")
    Iterable<TestingConfiguration> getAll(@NonNull UUID id);
}
