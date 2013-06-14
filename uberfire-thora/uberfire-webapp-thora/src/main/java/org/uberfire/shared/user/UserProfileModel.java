package org.uberfire.shared.user;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.shared.repository.RepositoryInfo;

@Portable
public class UserProfileModel {

    private String fullName;
    private String email;
    private String website;
    private String date;
    private int repos;
    private List<RepositoryInfo> lastContribs;

    public UserProfileModel() {
    }

    public UserProfileModel( String fullName,
                             String email,
                             String website,
                             String date,
                             int repos,
                             List<RepositoryInfo> lastContribs ) {
        this.fullName = fullName;
        this.email = email;
        this.website = website;
        this.date = date;
        this.repos = repos;
        this.lastContribs = lastContribs;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getMemberSince() {
        return date;
    }

    public int getPublicRepos() {
        return repos;
    }

    public List<RepositoryInfo> getLatestContributions() {
        return lastContribs;
    }

}

