package com.master.RetryPolicy.controller;

import com.master.RetryPolicy.entity.ProfileTable;
import com.master.RetryPolicy.service.ProfileTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ProfileTableController {

    @Autowired
    private ProfileTableService profileTableService;


    @GetMapping("/profile/{id}")
    public ResponseEntity<String> getItem(@PathVariable UUID id) {
        String profileTable = profileTableService.getProfile(id);
        return new ResponseEntity<>(profileTable, HttpStatus.OK);
    }

    @GetMapping("/profile/count")
    public ResponseEntity<Long> countProfile() {
        Long count = profileTableService.countProfile();
        if (count == -1) {
            return new ResponseEntity<>(count, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(count, HttpStatus.OK);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<UUID> createItem(@RequestBody ProfileTable item) {
        UUID newProfileID = profileTableService.createProfile(item);
        if (newProfileID != null) {
            return new ResponseEntity<>(newProfileID, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<Boolean> updateItem(@PathVariable UUID id, @RequestBody ProfileTable item) {
        boolean updatedProfileSuccess = profileTableService.updateProfile(id, item);
        if (updatedProfileSuccess) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Boolean> deleteItem(@PathVariable UUID id) {
        boolean deletedProfileSuccess = profileTableService.deleteProfile(id);
        if (deletedProfileSuccess) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
