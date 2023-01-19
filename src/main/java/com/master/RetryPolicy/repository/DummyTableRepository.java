package com.master.RetryPolicy.repository;

import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import com.master.RetryPolicy.entity.DummyTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DummyTableRepository extends CassandraRepository<DummyTable, UUID> {

    @Query("SELECT * FROM store.dummytable WHERE email=?0 ALLOW FILTERING")
    Iterable<DummyTable> findByEmail(String email);

    @Query("UPDATE store.dummytable SET birthday=?1 WHERE id=?0 IF EXISTS")
    DefaultRow updateBirthday(UUID id, LocalDate birthday);
}
