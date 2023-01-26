package com.master.RetryPolicy.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Table
public class Profile {
    @PrimaryKey
    private final UUID id;
    private final String name;
    private final String surname;
    private final String email;

    //@JsonFormat(pattern = "dd/mm/yyyy", shape = JsonFormat.Shape.STRING)
    private final LocalDate birthday;

    @PersistenceCreator
    public Profile(UUID id, String name, String surname, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
    }

    @JsonCreator
    public Profile(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("surname") String surname,
                   @JsonProperty("email") String email, @JsonProperty("birthday") String birthday) {
        if (Objects.equals(id, "")) {
            this.id = UUID.randomUUID();
        }
        else {
            this.id = UUID.fromString(id);
        }
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = LocalDate.of(
                Integer.parseInt(birthday.substring(6)),
                Integer.parseInt(birthday.substring(3, 5)),
                Integer.parseInt(birthday.substring(0, 2))
                );
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
        return "ProfileTableElement {\n" +
                "ID='" + id + "',\n" +
                "email='" + email + "',\n" +
                "name='" + name + "',\n" +
                "surname='" + surname + "',\n" +
                "birthday='" + birthday + "'\n" +
                "}\n";
    }

    public JsonNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", this.id.toString());
        json.put("name", this.name);
        json.put("surname", this.surname);
        json.put("email", this.email);
        json.put("birthday", this.birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return json;
    }
}
