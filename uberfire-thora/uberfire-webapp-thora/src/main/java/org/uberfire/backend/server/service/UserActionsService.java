package org.uberfire.backend.server.service;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.shared.repository.RepositoryInfo;

@ApplicationScoped
public class UserActionsService {

    private static final XStream xs = new XStream();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private UserServicesImpl userServices;

    @Inject
    private RepositoryAppServiceImpl repositoryAppService;

    public void store( final String owner,
                       final RepositoryInfo repo ) {
        final List<RepositoryInfo> repos = getRepoList( owner );
        repos.add( repo );

        final Path repoInfo = userServices.buildPath( owner, "repositories", "user_repos.xml" );

        ioService.write( repoInfo, xs.toXML( repos ) );
    }

    public List<RepositoryInfo> getRepoList( final String owner ) {
        final Path repoInfo = userServices.buildPath( owner, "repositories", "user_repos.xml" );

        if ( ioService.exists( repoInfo ) ) {
            final String xml = ioService.readAllString( repoInfo );
            return (ArrayList<RepositoryInfo>) xs.fromXML( xml );
        }

        return new ArrayList<RepositoryInfo>();
    }

    public void storeLastContrib( final String user,
                                  final String repo ) {
        storeLastContrib( user, repositoryAppService.getRepositoryInfo( repo ) );
    }

    public List<RepositoryInfo> getLastContribs( final String user ) {
        final Path actionsPath = userServices.buildPath( user, "repositories", "user_actions.xml" );

        if ( ioService.exists( actionsPath ) ) {
            final String xml = ioService.readAllString( actionsPath );
            return (FixedSizeArrayList<RepositoryInfo>) xs.fromXML( xml );
        }

        return new FixedSizeArrayList<RepositoryInfo>();
    }

    public void storeLastContrib( final String user,
                                  final RepositoryInfo repositoryInfo ) {
        final Path actionsPath = userServices.buildPath( user, "repositories", "user_actions.xml" );

        final List<RepositoryInfo> actions = getLastContribs( user );

        actions.add( repositoryInfo );

        ioService.write( actionsPath, xs.toXML( actions ) );
    }

}
