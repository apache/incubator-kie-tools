package org.uberfire.backend.server.repositories;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@Service
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    private Map<String, Repository> configuredRepositories = new HashMap<String, Repository>();

    @PostConstruct
    public void loadRepositories() {
        final List<ConfigGroup> repoConfigs = configurationService.getConfiguration( ConfigType.REPOSITORY );
        if ( !( repoConfigs == null || repoConfigs.isEmpty() ) ) {
            for ( ConfigGroup config : repoConfigs ) {
                final Repository repository = repositoryFactory.newRepository( config );
                configuredRepositories.put( repository.getAlias(),
                                            repository );
            }
        }
    }

    @Override
    public Repository getRepository( final String alias ) {
        return configuredRepositories.get( alias );
    }

    @Override
    public Collection<Repository> getRepositories() {
        return Collections.unmodifiableCollection( configuredRepositories.values() );
    }

    @Override
    public void createRepository( final String scheme,
                                  final String alias,
                                  final String username,
                                  final String password ) {
        //Make a ConfigGroup for the new repository
        final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup( ConfigType.REPOSITORY,
                                                                                  alias,
                                                                                  "" );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.SCHEME,
                                                                            scheme ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.USER_NAME,
                                                                            username ) );
        repositoryConfig.addConfigItem( configurationFactory.newSecuredConfigItem( EnvironmentParameters.USER_PASSWORD,
                                                                                   password ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.LOCATION,
                                                                            Location.LOCAL.name() ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.INITIALIZE,
                                                                            Boolean.TRUE ) );
        addRepository( repositoryConfig );
    }

    @Override
    public void cloneRepository( final String scheme,
                                 final String alias,
                                 final String origin,
                                 final String username,
                                 final String password ) {
        //Make a ConfigGroup for the new repository
        final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup( ConfigType.REPOSITORY,
                                                                                  alias,
                                                                                  "" );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.SCHEME,
                                                                            scheme ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.USER_NAME,
                                                                            username ) );
        repositoryConfig.addConfigItem( configurationFactory.newSecuredConfigItem( EnvironmentParameters.USER_PASSWORD,
                                                                                   password ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.LOCATION,
                                                                            Location.REMOTE.name() ) );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( EnvironmentParameters.ORIGIN,
                                                                            origin ) );
        addRepository( repositoryConfig );
    }

    //Save the definition
    private void addRepository( final ConfigGroup repositoryConfig ) {
        final Repository repository = repositoryFactory.newRepository( repositoryConfig );
        configurationService.addConfiguration( repositoryConfig );
        configuredRepositories.put( repository.getAlias(),
                                    repository );
    }
}
