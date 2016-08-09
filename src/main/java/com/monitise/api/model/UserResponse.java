package com.monitise.api.model;

import com.monitise.entity.JobTitle;
import com.monitise.entity.Organization;
import com.monitise.entity.User;

import java.util.ArrayList;
import java.util.List;


public class UserResponse {

    private int id;
    private String name;
    private String surname;
    private JobTitle jobTitle;
    private Role role;
    private OrganizationResponse organization;

    public UserResponse(User user) {
        id = user.getId();
        name = user.getName();
        surname = user.getSurname();
        jobTitle = user.getJobTitle();
        role = user.getRole();
        // TODO: Implement organization model.
        organization = OrganizationResponse.fromOrganization(user.getOrganization());
    }


    public static UserResponse fromUser(User user) {
        return new UserResponse(user);
    }

    public static List<UserResponse> fromUserList(List<User> users) {
        List<UserResponse> responses = new ArrayList<>();
        for(User user : users){
            UserResponse current = fromUser(user);
            responses.add(current);
        }
        return responses;
    }

    // region Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public Role getRole() {
        return role;
    }

    public OrganizationResponse getOrganization() {
        return organization;
    }

    // endregion

    // region Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setOrganization(OrganizationResponse organization) {
        this.organization = organization;
    }

    // endregion
}
