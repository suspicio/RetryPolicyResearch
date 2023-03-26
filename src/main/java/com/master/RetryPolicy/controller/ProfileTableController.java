package com.master.RetryPolicy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.master.RetryPolicy.entity.Profile;
import com.master.RetryPolicy.service.ProfileTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class ProfileTableController {

    @Autowired
    private ProfileTableService profileTableService;


    @GetMapping("/profile/{id}")
    public ResponseEntity<JsonNode> getProfile(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        Future<JsonNode> profileTable = profileTableService.getProfile(id);
        JsonNode profileTableResp = profileTable.get();
        return new ResponseEntity<>(profileTableResp, HttpStatus.OK);
    }

    @GetMapping("/profile/ids")
    public ResponseEntity<List<String>> getProfilesIds() throws ExecutionException, InterruptedException {
        Future<List<String>> profilesIds = profileTableService.getProfilesIds();
        List<String> profileIdsResp = profilesIds.get();
        return new ResponseEntity<>(profileIdsResp, HttpStatus.OK);
    }

    @GetMapping("/profile/count")
    public ResponseEntity<Long> countProfile() throws ExecutionException, InterruptedException {
        Future<Long> count = profileTableService.countProfile();
        Long countResp = count.get();
        if (countResp == -1) {
            return new ResponseEntity<>(countResp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(countResp, HttpStatus.OK);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<UUID> createProfile(@RequestBody Profile profile) throws ExecutionException, InterruptedException {
        Future<UUID> newProfileID = profileTableService.createProfile(profile);
        UUID newProfileIDResp = newProfileID.get();
        if (newProfileIDResp != null) {
            return new ResponseEntity<>(newProfileIDResp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Boolean> updateProfile(@RequestBody Profile profile) throws ExecutionException, InterruptedException {
        Future<Boolean> updatedProfile = profileTableService.updateProfile(profile.getId(), profile);
        Boolean updatedProfileSuccess = updatedProfile.get();
        if (updatedProfileSuccess) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Boolean> deleteProfile(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        Future<Boolean> deletedProfile = profileTableService.deleteProfile(id);
        Boolean deletedProfileSuccess = deletedProfile.get();
        if (deletedProfileSuccess) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }
}
