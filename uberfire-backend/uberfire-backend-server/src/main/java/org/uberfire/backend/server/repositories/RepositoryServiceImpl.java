package org.uberfire.backend.server.repositories;

import static org.uberfire.backend.server.config.ConfigType.REPOSITORY;
import static org.uberfire.backend.server.repositories.EnvironmentParameters.SCHEME;

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

import com.sun.tools.hat.internal.model.Root;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.vfs.Path;

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
    private Map<Path, Repository> rootToRepo = new HashMap<Path, Repository>();

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void loadRepositories() {
        final List<ConfigGroup> repoConfigs = configurationService.getConfiguration( REPOSITORY );
        if ( !( repoConfigs == null || repoConfigs.isEmpty() ) ) {
            for ( final ConfigGroup config : repoConfigs ) {
                final Repository repository = repositoryFactory.newRepository( config );
                configuredRepositories.put( repository.getAlias(), repository );
                rootToRepo.put( repository.getRoot(), repository );
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
    public Repository getRepository( final Path root ) {
        return rootToRepo.get( root );
    }

    @Override
    public Collection<Repository> getRepositories() {
        return new ArrayList<Repository>( configuredRepositories.values() );
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
        rootToRepo.put( repository.getRoot(), repository );
        return repository;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRole( Repository repository,
                         String role ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( repository.getAlias() );

        if ( thisRepositoryConfig != null ) {
            final ConfigItem<List> roles = thisRepositoryConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.updateConfiguration( thisRepositoryConfig );

            final Repository updatedRepo = repositoryFactory.newRepository( thisRepositoryConfig );
            configuredRepositories.put( updatedRepo.getAlias(), updatedRepo );
            rootToRepo.put( updatedRepo.getRoot(), updatedRepo );
        } else {
            throw new IllegalArgumentException( "Repository " + repository.getAlias() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRole( Repository repository,
                            String role ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( repository.getAlias() );

        if ( thisRepositoryConfig != null ) {
            final ConfigItem<List> roles = thisRepositoryConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.updateConfiguration( thisRepositoryConfig );

            final Repository updatedRepo = repositoryFactory.newRepository( thisRepositoryConfig );
            configuredRepositories.put( updatedRepo.getAlias(), updatedRepo );
            rootToRepo.put( updatedRepo.getRoot(), updatedRepo );
        } else {
            throw new IllegalArgumentException( "Repository " + repository.getAlias() + " not found" );
        }
    }

    protected ConfigGroup findRepositoryConfig( final String alias ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.REPOSITORY );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( alias ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void removeRepository( String alias ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( alias );

        if ( thisRepositoryConfig != null ) {
            configurationService.removeConfiguration( thisRepositoryConfig );
            final Repository repo = configuredRepositories.remove( alias );
            if ( repo != null ) {
                rootToRepo.remove( repo.getRoot() );
            }
        }

    }
}
