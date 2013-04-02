package org.uberfire.backend.server.repositories;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.PasswordService;

import static org.kie.commons.validation.Preconditions.*;

@ApplicationScoped
public class RepositoryFactoryImpl implements RepositoryFactory {

    @Inject
    private PasswordService secureService;

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "config",
                      repoConfig );
        final ConfigItem<String> scheme = repoConfig.getConfigItem( "scheme" );
        checkNotNull( "scheme",
                      scheme );

        Repository repository = null;
        if ( GitRepository.SCHEME.equals( scheme.getValue() ) ) {
            repository = new GitRepository( repoConfig.getName() );
            repository.addEnvironmentParameter( GitRepository.ORIGIN,
                                                repoConfig.getConfigItemValue( "origin" ) );
            repository.addEnvironmentParameter( GitRepository.USERNAME,
                                                repoConfig.getConfigItemValue( "username" ) );
            repository.addEnvironmentParameter( GitRepository.PASSWORD,
                                                secureService.decrypt( repoConfig.getConfigItemValue( "password" ) ) );

            if ( !repository.isValid() ) {
                throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
            }
        } else {
            throw new IllegalArgumentException( "Unrecognized scheme '" + scheme.getValue() + "'." );
        }
        return repository;
    }
}
