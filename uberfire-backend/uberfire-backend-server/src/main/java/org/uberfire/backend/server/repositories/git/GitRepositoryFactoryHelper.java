package org.uberfire.backend.server.repositories.git;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.repositories.PublicURI;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.impl.DefaultPublicURI;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.PasswordService;
import org.uberfire.backend.server.config.SecureConfigItem;
import org.uberfire.backend.server.repositories.EnvironmentParameters;
import org.uberfire.backend.server.repositories.RepositoryFactoryHelper;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.repositories.impl.git.GitRepository.*;
import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.commons.validation.Preconditions.*;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

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

        String branch = repoConfig.getConfigItemValue(EnvironmentParameters.BRANCH);
        if (branch == null) {
            branch =  "master";
        }

        final GitRepository repo = new GitRepository( repoConfig.getName() );
        repo.setBranch(branch);

        for ( final ConfigItem item : repoConfig.getItems() ) {
            if ( item instanceof SecureConfigItem ) {
                repo.addEnvironmentParameter( item.getName(), secureService.decrypt( item.getValue().toString() ) );
            } else {
                repo.addEnvironmentParameter( item.getName(), item.getValue() );
            }
        }

        if ( !repo.isValid() ) {
            throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
        }

        FileSystem fs = null;
        URI uri = null;
        try {
            uri = URI.create( repo.getUri() );
            fs = ioService.newFileSystem( uri, new HashMap<String, Object>( repo.getEnvironment() ) {{
                if ( !repo.getEnvironment().containsKey( "origin" ) ) {
                    put( "init", true );
                }
            }} );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fs = ioService.getFileSystem( uri );
        } catch ( final Throwable ex ) {
            throw new RuntimeException( ex.getCause().getMessage(), ex );
        }

        Path defaultRoot = fs.getRootDirectories().iterator().next();
        for ( final Path path : fs.getRootDirectories() ) {
            String gitBranch = getBranchName(path);
            if ( gitBranch.equals(branch) ) {
                defaultRoot = path;
                break;
            }
        }
        Set<String> branches = new HashSet<String>();
        // collect all branches
        for ( final Path path : fs.getRootDirectories() ) {
            String gitBranch = getBranchName(path);
            branches.add(gitBranch);
        }
        repo.setBranches(branches);


        repo.setRoot( convert( defaultRoot ) );
        final String[] uris = fs.toString().split( "\\r?\\n" );
        final List<PublicURI> publicURIs = new ArrayList<PublicURI>( uris.length );

        for ( final String s : uris ) {
            final int protocolStart = s.indexOf( "://" );
            final PublicURI publicURI;
            if ( protocolStart > 0 ) {
                publicURI = new DefaultPublicURI( s.substring( 0, protocolStart ), s );
            } else {
                publicURI = new DefaultPublicURI( s );
            }
            publicURIs.add( publicURI );
        }
        repo.setPublicURIs( publicURIs );

        return repo;
    }

    protected String getBranchName(final Path path) {
        String gitBranch = path.toUri().getAuthority();

        if (gitBranch.indexOf("@") != -1) {
            return gitBranch.split("@")[0];
        }

        return gitBranch;
    }
}
