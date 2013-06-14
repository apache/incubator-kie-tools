package org.uberfire.shared.repository;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewRepositoryInfo {

    private RepositoryInfo repositoryInfo;

    public NewRepositoryInfo() {
    }

    public NewRepositoryInfo( RepositoryInfo repositoryInfo ) {
        this.repositoryInfo = repositoryInfo;
    }

    public RepositoryInfo getRepositoryInfo() {
        return repositoryInfo;
    }
}
