package org.uberfire.backend.server.repositories;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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

    @Inject
    @Any
    private Instance<RepositoryFactoryHelper> helpers;

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig",
                      repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem",
                      schemeConfigItem );

        //Find a Helper that can create a repository
        Repository repository = null;
        for ( RepositoryFactoryHelper helper : helpers ) {
            if ( helper.accept( repoConfig ) ) {
                repository = helper.newRepository( repoConfig );
                break;
            }
        }

        //Check one was created
        if ( repository == null ) {
            throw new IllegalArgumentException( "Unrecognized scheme '" + schemeConfigItem.getValue() + "'." );
        }

        return repository;
    }

}
