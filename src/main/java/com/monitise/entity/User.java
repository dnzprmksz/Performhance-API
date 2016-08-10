package com.monitise.entity;

import com.monitise.api.model.AddUserRequest;
import com.monitise.api.model.Role;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String surname;
    @ManyToOne
    private JobTitle jobTitle;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    private Organization organization;
    @ManyToOne
    private Team team;
    @ManyToMany
    private List<Criteria> criteriaList;
    private String username;
    private String password;

    public User() {}

    public User(String name, String surname, Organization organization) {
        this.name = name;
        this.surname = surname;
        this.organization = organization;
        role = Role.EMPLOYEE;
    }

    public User(String name, String surname, Organization organization, Role role) {
        this.name = name;
        this.surname = surname;
        this.organization = organization;
        this.role = role;
    }

    public User(AddUserRequest userRequest, Organization organization, String username, String password) {
        name = userRequest.getName();
        surname = userRequest.getSurname();
        this.organization = organization;
        this.password = password;
        this.username = username;
        role = Role.EMPLOYEE;
    }

    // region Getters

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public int getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    // endregion

    // region Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCriteriaList(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    // endregion

}