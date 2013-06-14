package org.uberfire.backend.server.repositories.git;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.PasswordService;
import org.uberfire.backend.server.config.SecureConfigItem;
import org.uberfire.backend.server.repositories.EnvironmentParameters;
import org.uberfire.backend.server.repositories.RepositoryFactoryHelper;
import org.uberfire.backend.server.util.Paths;

import static org.kie.commons.validation.Preconditions.*;
import static org.uberfire.backend.repositories.impl.git.GitRepository.*;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private PasswordService secureService;

    @Override
    public boolean accept( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );
        return SCHEME.equals( schemeConfigItem.getValue() );
    }

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );

        final GitRepository repo = new GitRepository( repoConfig.getName() );

        for ( final ConfigItem item : repoConfig.getItems() ) {
            if ( item instanceof SecureConfigItem ) {
                repo.addEnvironmentParameter( item.getName(), secureService.decrypt( item.getValue().toString() ) );
            } else {
                repo.addEnvironmentParameter( item.getName(), item.getValue() );
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> roles = repoConfig.getConfigItem( "security:roles" );
        if ( roles != null ) {
            for ( String role : roles.getValue() ) {
                repo.getRoles().add( role );
            }
        }

        if ( !repo.isValid() ) {
            throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
        }

        FileSystem fs = null;
        URI uri = null;
        try {
            uri = new URI( repo.getUri() );
            fs = ioService.newFileSystem( uri, repo.getEnvironment() );
        } catch ( URISyntaxException e ) {
        } catch ( final FileSystemAlreadyExistsException e ) {
            fs = ioService.getFileSystem( uri );
        }

        repo.setRoot( paths.convert( fs.getRootDirectories().iterator().next() ) );
        repo.setPublicUri( fs.toString() );

        return repo;
    }
}
