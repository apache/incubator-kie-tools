package org.uberfire.shared.repository;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RepositoryAppService {

    List<RepositoryInfo> getUserRepositories( final String userName );

    RepositoryInfo createRepository( final String owner,
                                     final String name,
                                     final String description,
                                     final boolean init );

    RepositoryInfo mirrorRepository( final String owner,
                                     final String name,
                                     final String description,
                                     final String origin );

    RepositoryInfo getRepositoryInfo( final String name );

    boolean repositoryAlreadyExists( final String name );

    List<RepositoryInfo> getAllRepositories();
}
