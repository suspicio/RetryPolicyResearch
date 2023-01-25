package com.master.RetryPolicy.repository;

import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import com.master.RetryPolicy.entity.ProfileTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ProfileTableRepository extends CassandraRepository<ProfileTable, UUID> {

    @Query("UPDATE store.dummytable SET name=?1 surname=?2 email=?3 birthday=?4 WHERE id=?0 IF EXISTS")
    DefaultRow updateProfile(UUID id, String name, String surname, String email, LocalDate birthday);
}
