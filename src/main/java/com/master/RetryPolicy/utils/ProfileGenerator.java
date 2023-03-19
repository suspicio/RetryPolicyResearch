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
        String day = Integer.toString(randomDay);
        if (day.length() == 1)
            day = "0" + day;
        String month = Integer.toString(randomMonth);
        if (month.length() == 1)
            month = "0" + month;
        String year = Integer.toString(randomYear);
        String randomBirthday = day + '-' + month + '-' + year;

        return new Profile(UUID.randomUUID(), randomFirstName, randomLastName, randomEmail, randomBirthday);
    }

    public static @NotNull Profile generateRandomProfileWithID(UUID id) {
        Profile profile = generateRandomProfile();
        profile.setUUID(id);
        return profile;
    }
}
