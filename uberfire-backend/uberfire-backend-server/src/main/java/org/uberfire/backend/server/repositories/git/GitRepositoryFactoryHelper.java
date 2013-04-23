package org.uberfire.backend.server.repositories.git;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.PasswordService;
import org.uberfire.backend.server.repositories.EnvironmentParameters;
import org.uberfire.backend.server.repositories.Location;
import org.uberfire.backend.server.repositories.RepositoryFactoryHelper;

import static org.kie.commons.validation.Preconditions.*;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    @Inject
    private PasswordService secureService;

    @Override
    public boolean accept( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig",
                      repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem",
                      schemeConfigItem );
        return RemoteGitRepository.SCHEME.equals( schemeConfigItem.getValue() );
    }

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig",
                      repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem",
                      schemeConfigItem );
        final ConfigItem<String> locationConfigItem = repoConfig.getConfigItem( EnvironmentParameters.LOCATION );
        checkNotNull( "locationConfigItem",
                      locationConfigItem );

        Repository repository = null;
        final Location location = Location.valueOf( locationConfigItem.getValue() );
        switch ( location ) {
            case LOCAL:
                repository = new LocalGitRepository( repoConfig.getName() );
                break;
            case REMOTE:
                repository = new RemoteGitRepository( repoConfig.getName() );
                addEnvironmentParameter( repository,
                                         repoConfig,
                                         EnvironmentParameters.ORIGIN );
        }
        addEnvironmentParameter( repository,
                                 repoConfig,
                                 EnvironmentParameters.USER_NAME );
        addSecureEnvironmentParameter( repository,
                                       repoConfig,
                                       EnvironmentParameters.USER_PASSWORD );

        if ( !repository.isValid() ) {
            throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
        }

        return repository;
    }

    private void addEnvironmentParameter( final Repository repository,
                                          final ConfigGroup repoConfig,
                                          final String name ) {
        final String value = repoConfig.getConfigItemValue( name );
        if ( value == null ) {
            return;
        }
        repository.addEnvironmentParameter( name,
                                            value );
    }

    private void addSecureEnvironmentParameter( final Repository repository,
                                                final ConfigGroup repoConfig,
                                                final String name ) {
        final String encryptedValue = repoConfig.getConfigItemValue( name );
        if ( encryptedValue == null ) {
            return;
        }
        final String decryptedValue = secureService.decrypt( encryptedValue );
        repository.addEnvironmentParameter( name,
                                            decryptedValue );
    }

}
