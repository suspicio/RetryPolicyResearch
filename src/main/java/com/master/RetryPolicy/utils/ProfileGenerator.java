package com.master.RetryPolicy.utils;

import com.master.RetryPolicy.entity.Profile;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class ProfileGenerator {
    public static @NotNull Profile generateRandomProfile() {
        String[] firstNames = {"John", "Jane", "Jim", "Jessica", "Michael", "Emily"};
        String[] lastNames = {"Smith", "Doe", "Johnson", "Williams", "Brown", "Davis"};
        String randomFirstName = firstNames[(int) Math.floor(Math.random() * firstNames.length)];
        String randomLastName = lastNames[(int) Math.floor(Math.random() * lastNames.length)];
        String randomEmail = randomFirstName.toLowerCase() + "." + randomLastName.toLowerCase() + "@example.com";
        Random random = new Random();
        int randomDay = random.nextInt(29) + 1;
        int randomMonth = random.nextInt(12) + 1;
        int randomYear = random.nextInt(50) + 1950;
        LocalDate randomBirthday = LocalDate.of(randomYear, randomMonth, randomDay);

        return new Profile(UUID.fromString(""), randomFirstName, randomLastName, randomEmail, randomBirthday);
    }

    public static @NotNull Profile generateRandomProfileWithID(UUID id) {
        Profile profile = generateRandomProfile();
        profile.setUUID(id);
        return profile;
    }
}
