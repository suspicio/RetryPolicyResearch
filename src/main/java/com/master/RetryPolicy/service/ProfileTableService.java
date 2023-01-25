package com.master.RetryPolicy.service;

import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import com.master.RetryPolicy.entity.ProfileTable;
import com.master.RetryPolicy.repository.ProfileTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.*;

@EnableAsync
@Service
public class ProfileTableService {
    @Autowired
    private ProfileTableRepository profileTableRepository;
    public UUID createProfile(ProfileTable profileTable) {
        long startTime = System.nanoTime();
        try {
            ProfileTable profileTableNew = new ProfileTable(UUID.randomUUID(), profileTable.getName(),
                    profileTable.getSurname(), profileTable.getEmail(),
                    profileTable.getBirthday());
            profileTableRepository.save(profileTableNew);
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            System.out.println("Response time Save: " + responseTime + "ms");
            return profileTable.getId();
        } catch (Exception e) {
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            System.out.println("Response time Save: " + responseTime + "ms");
            System.err.println(e);
            return null;
        }
    }

    public long countProfile() {
        long startTime = System.nanoTime();
        long count = -1;
        try {
            count = profileTableRepository.count();
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.println("Response time COUNT: " + responseTime + "ms\nResponse: " + count);
        return count;
    }

    public String getProfile(UUID id) {
        long startTime = System.nanoTime();
        Optional<ProfileTable> resp = Optional.empty();
        try {
            resp = profileTableRepository.findById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.print("Response time Read: " + responseTime);
        return resp.map(ProfileTable::toString).orElse(null);
    }

    public Boolean updateProfile(UUID id, ProfileTable profile) {
        long startTime = System.nanoTime();

        try {
            DefaultRow df = profileTableRepository.updateProfile(id, profile.getName(), profile.getSurname(), profile.getEmail(), profile.getBirthday());
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            System.out.println("Response time Update: " + responseTime + "ms\nResponse: " + df);
            return true;
        } catch (Exception e) {
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1000000;
            System.out.println("Response time Update: " + responseTime + "ms");
            System.err.println(e);
            return false;
        }
    }

    public boolean deleteProfile(UUID id) {
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
        System.out.print("Response time Read: " + responseTime);

        return resp;
    }
}
