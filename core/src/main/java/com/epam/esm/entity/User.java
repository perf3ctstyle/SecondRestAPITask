package com.epam.esm.entity;

import org.springframework.hateoas.RepresentationModel;

public class User extends RepresentationModel<User> {

    private Long id;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
