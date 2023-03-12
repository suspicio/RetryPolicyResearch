package com.master.RetryPolicy.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Profile {
    private UUID id;
    private final String name;
    private final String surname;
    private final String email;
    private final LocalDate birthday;

    public Profile(UUID id, String name, String surname, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
    }

    public void setUUID(UUID id) {
        this.id = id;
    }
}