package com.epam.esm.entity;

import com.epam.esm.audit.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@EntityListeners(AuditListener.class)
public class User extends RepresentationModel<User> implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
