package org.uberfire.backend.server.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

import static org.uberfire.backend.server.config.ConfigType.*;
import static org.uberfire.backend.server.repositories.EnvironmentParameters.*;

@Service
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private Event<NewRepositoryEvent> event;

    private Map<String, Repository> configuredRepositories = new HashMap<String, Repository>();
    private List<Repository> configuredRepositoriesList = new ArrayList<Repository>();

    @PostConstruct
    public void loadRepositories() {
        final List<ConfigGroup> repoConfigs = configurationService.getConfiguration( REPOSITORY );
        if ( !( repoConfigs == null || repoConfigs.isEmpty() ) ) {
            for ( final ConfigGroup config : repoConfigs ) {
                final Repository repository = repositoryFactory.newRepository( config );
                configuredRepositories.put( repository.getAlias(),
                                            repository );
                configuredRepositoriesList.add( repository );
            }
        }

        ioService.onNewFileSystem( new IOService.NewFileSystemListener() {
            @Override
            public void execute( final FileSystem newFileSystem,
                                 final String scheme,
                                 final String name,
                                 final Map<String, ?> env ) {
                if ( getRepository( name ) == null ) {
                    createRepository( scheme, name, (Map<String, Object>) env );
                }
            }
        } );
    }

    @Override
    public Repository getRepository( final String alias ) {
        return configuredRepositories.get( alias );
    }

    @Override
    public Collection<Repository> getRepositories() {
        return configuredRepositoriesList;
    }

    @Override
    public Repository createRepository( final String scheme,
                                        final String alias,
                                        final Map<String, Object> env ) {

        final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup( REPOSITORY,
                                                                                  alias,
                                                                                  "" );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                            new ArrayList<String>() ) );

        if ( !env.containsKey( SCHEME ) ) {
            repositoryConfig.addConfigItem( configurationFactory.newConfigItem( SCHEME,
                                                                                scheme ) );
        }
        for ( final Map.Entry<String, Object> entry : env.entrySet() ) {
            if ( entry.getKey().startsWith( "crypt:" ) ) {
                repositoryConfig.addConfigItem( configurationFactory.newSecuredConfigItem( entry.getKey(),
                                                                                           entry.getValue().toString() ) );
            } else {
                repositoryConfig.addConfigItem( configurationFactory.newConfigItem( entry.getKey(),
                                                                                    entry.getValue() ) );
            }
        }

        final Repository repo = createRepository( repositoryConfig );

        event.fire( new NewRepositoryEvent( repo ) );

        return repo;
    }

    //Save the definition
    private Repository createRepository( final ConfigGroup repositoryConfig ) {
        final Repository repository = repositoryFactory.newRepository( repositoryConfig );
        configurationService.addConfiguration( repositoryConfig );
        configuredRepositories.put( repository.getAlias(),
                                    repository );
        configuredRepositoriesList.add( repository );
        return repository;
    }
}
