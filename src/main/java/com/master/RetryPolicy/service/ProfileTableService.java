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
        try {
            profileTableRepository.save(profile);
            return new AsyncResult<>(profile.getId());
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Future<Long> countProfile() {
        long count = -1;
        try {
            count = profileTableRepository.count();
        } catch (Exception e) {
            System.err.println(e);
        }
        return new AsyncResult<>(count);
    }

    public Future<JsonNode> getProfile(UUID id) {
        Iterable<Profile> resp = null;
        final JsonNode[] respJson = new JsonNode[1];
        try {
            resp = profileTableRepository.findProfileById(id);
            Consumer<Profile> collectInner = profile -> respJson[0] = profile.toJSON();
            resp.forEach(collectInner);
        } catch (Exception e) {
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
        try {
            DefaultRow df = profileTableRepository.updateProfile(id, profile.getName(), profile.getSurname(), profile.getEmail(), profile.getBirthday());
            return new AsyncResult<>(true);
        } catch (Exception e) {
            return new AsyncResult<>(false);
        }
    }

    public Future<Boolean> deleteProfile(UUID id) {
        boolean resp = true;
        try {
            profileTableRepository.deleteById(id);
        } catch (Exception e) {
            resp = false;
            System.err.println(e);
        }
        return new AsyncResult<>(resp);
    }
}
