package com.master.RetryPolicy.service;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.Profile;
import com.master.RetryPolicy.repository.ProfileTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@EnableAsync
@Async
@Service
public class ProfileTableService {
    @Autowired
    private ProfileTableRepository profileTableRepository;

    @PostConstruct
    private void deleteAll() {
        profileTableRepository.deleteAll();
    }

    public Future<UUID> createProfile(Profile profile) {
        long startTime = System.nanoTime();
        System.out.println(profile.toString());
        try {
            profileTableRepository.save(profile);
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.println("Response time Save: " + responseTime + "ms");
            return new AsyncResult<>(profile.getId());
        } catch (Exception e) {
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.println("Response time Save: " + responseTime + "ms");
            System.err.println(e);
            return null;
        }
    }

    public Future<Long> countProfile() {
        long startTime = System.nanoTime();
        long count = -1;
        try {
            count = profileTableRepository.count();
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        //System.out.println("Response time COUNT: " + responseTime + "ms\nResponse: " + count);
        return new AsyncResult<>(count);
    }

    public Future<JsonNode> getProfile(UUID id) {
        long startTime = System.nanoTime();
        Iterable<Profile> resp = null;
        final JsonNode[] respJson = new JsonNode[1];
        try {
            //System.out.println("ID: " + id);
            resp = profileTableRepository.findProfileById(id);
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.println("Response time Read: " + responseTime);
            Consumer<Profile> collectInner = profile -> respJson[0] = profile.toJSON();
            resp.forEach(collectInner);
        } catch (Exception e) {
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.println("Response time Read: " + responseTime);
            System.err.println(e);
        }

        if (resp != null) {
            return new AsyncResult<>(respJson[0]);
        }

        return new AsyncResult<>(null);
    }

    public Future<List<String>> getProfilesIds() {
        List<Profile> resp;
        final List<String> respList = new ArrayList<>();
        try {
            resp = profileTableRepository.findAll();
            Consumer<Profile> collectInner = df -> respList.add(df.getId().toString());
            resp.forEach(collectInner);
        } catch (Exception e) {
            System.err.println(e);
        }

        return new AsyncResult<>(respList);
    }

    public Future<Boolean> updateProfile(UUID id, Profile profile) {
        long startTime = System.nanoTime();

        try {
            DefaultRow df = profileTableRepository.updateProfile(id, profile.getName(), profile.getSurname(), profile.getEmail(), profile.getBirthday());
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.print("Response time Update: " + responseTime + "ms\nResponse: ");
            boolean response = true;
            for (int i = 0; i < ((Row) df).getColumnDefinitions().size(); i++) {
                String columnName = ((Row) df).getColumnDefinitions().get(i).getName().toString();
                Object columnValue = df.getObject(i);
                assert columnValue != null;
                response = Boolean.parseBoolean(columnValue.toString());
                //System.out.println(columnName + ": " + columnValue);
            }
            return new AsyncResult<>(response);
        } catch (Exception e) {
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            //System.out.println("Response time Update: " + responseTime + "ms");
            System.err.println(e);
            return new AsyncResult<>(false);
        }
    }

    public Future<Boolean> deleteProfile(UUID id) {
        long startTime = System.nanoTime();
        boolean resp = true;
        try {
            profileTableRepository.deleteById(id);
        } catch (Exception e) {
            resp = false;
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        //System.out.print("Response time Read: " + responseTime);

        return new AsyncResult<>(resp);
    }
}
