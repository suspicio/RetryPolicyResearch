package com.master.RetryPolicy.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table
public class DummyTable {
    @PrimaryKey
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;

    public DummyTable(UUID id, String name, String surname, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String toString() {
        return "DummyTableElement {\n" +
                "ID='" + id + "',\n" +
                "email='" + email + "',\n" +
                "name='" + name + "',\n" +
                "surname='" + surname + "',\n" +
                "birthday='" + birthday + "'\n" +
                "}\n";
    }
}
