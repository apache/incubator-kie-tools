package org.uberfire.backend.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.shared.repository.NewRepositoryInfo;
import org.uberfire.shared.repository.RepositoryAlreadyExists;
import org.uberfire.shared.repository.RepositoryAppService;
import org.uberfire.shared.repository.RepositoryInfo;

@Service
@ApplicationScoped
public class RepositoryAppServiceImpl implements RepositoryAppService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private UserActionsService userServices;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private Event<NewRepositoryInfo> newRepositoryInfoEvent;

    @Override
    public List<RepositoryInfo> getUserRepositories( String userName ) {
        return userServices.getRepoList( userName );
    }

    @Override
    public RepositoryInfo createRepository( final String owner,
                                            final String name,
                                            final String description,
                                            final boolean init ) {
        if ( repositoryService.getRepository( name ) != null ) {
            throw new RepositoryAlreadyExists( name );
        }

        final Repository newRepository = repositoryService.createRepository( "git", name, new HashMap<String, Object>() {{
            put( "owner", owner );
            put( "desc", description );
            put( "init", init );
        }} );

        final RepositoryInfo repositoryInfo = new RepositoryInfo( owner, name, description, newRepository.getPublicUri() );

        userServices.store( owner, repositoryInfo );

        newRepositoryInfoEvent.fire( new NewRepositoryInfo( repositoryInfo ) );

        userServices.storeLastContrib( owner, repositoryInfo );

        return repositoryInfo;
    }

    @Override
    public RepositoryInfo mirrorRepository( final String owner,
                                            final String name,
                                            final String description,
                                            final String origin ) {
        if ( repositoryService.getRepository( name ) != null ) {
            throw new RepositoryAlreadyExists( name );
        }

        final Repository newRepository = repositoryService.createRepository( "git", name, new HashMap<String, Object>() {{
            put( "owner", owner );
            put( "origin", origin );
            put( "desc", description );
        }} );

        final RepositoryInfo repositoryInfo = new RepositoryInfo( owner, name, description, newRepository.getPublicUri(), origin );

        userServices.store( owner, repositoryInfo );

        newRepositoryInfoEvent.fire( new NewRepositoryInfo( repositoryInfo ) );

        userServices.storeLastContrib( owner, repositoryInfo );

        return repositoryInfo;
    }

    @Override
    public RepositoryInfo getRepositoryInfo( final String name ) {
        final Repository repository = repositoryService.getRepository( name );

        final String origin = repository.getEnvironment().get( "origin" ) != null ? repository.getEnvironment().get( "origin" ).toString() : "";

        return new RepositoryInfo( repository.getEnvironment().get( "owner" ).toString(), name, repository.getEnvironment().get( "desc" ).toString(), repository.getPublicUri(), origin, repository.getRoot() );
    }

    @Override
    public boolean repositoryAlreadyExists( String name ) {
        return repositoryService.getRepository( name ) != null;
    }

    @Override
    public List<RepositoryInfo> getAllRepositories() {
        final List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();
        for ( final Repository repository : repositoryService.getRepositories() ) {
            final String origin = repository.getEnvironment().get( "origin" ) != null ? repository.getEnvironment().get( "origin" ).toString() : "";
            result.add( new RepositoryInfo( repository.getEnvironment().get( "owner" ).toString(), repository.getAlias(), repository.getEnvironment().get( "desc" ).toString(), repository.getPublicUri(), origin, repository.getRoot() ) );
        }

        return result;
    }

}
