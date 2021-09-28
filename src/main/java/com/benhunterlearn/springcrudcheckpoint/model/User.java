package com.benhunterlearn.springcrudcheckpoint.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @JsonIgnore
    private String password;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
