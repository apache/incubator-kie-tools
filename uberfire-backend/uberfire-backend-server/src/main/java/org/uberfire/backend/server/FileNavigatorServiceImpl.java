package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.ocpsoft.prettytime.PrettyTime;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.navigator.DataContent;
import org.uberfire.navigator.FileNavigatorService;
import org.uberfire.navigator.NavigatorContent;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class FileNavigatorServiceImpl implements FileNavigatorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private RepositoryService repositoryService;

    private final PrettyTime p = new PrettyTime();

    @Override
    public NavigatorContent listContent( final org.uberfire.backend.vfs.Path _path ) {
        final ArrayList<DataContent> result = new ArrayList<DataContent>();
        final ArrayList<org.uberfire.backend.vfs.Path> breadcrumbs = new ArrayList<org.uberfire.backend.vfs.Path>();

        Path path = paths.convert( _path );
        final DirectoryStream<Path> stream = ioService.newDirectoryStream( path );

        for ( final Path activePath : stream ) {
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( activePath, VersionAttributeView.class );
            int index = versionAttributeView.readAttributes().history().records().size() - 1;

            final String authorEmail = versionAttributeView.readAttributes().history().records().get( index ).email();
            final String author = versionAttributeView.readAttributes().history().records().get( index ).author();
            final String comment = versionAttributeView.readAttributes().history().records().get( index ).comment();

            final String time = p.format( new Date( Files.getLastModifiedTime( activePath ).toMillis() ) );
            result.add( new DataContent( Files.isDirectory( activePath ), comment, author, authorEmail, time, paths.convert( activePath ) ) );
        }

        sort( result, new Comparator<DataContent>() {
            @Override
            public int compare( final DataContent dataContent,
                                final DataContent dataContent2 ) {

                int fileCompare = dataContent.getPath().getFileName().toLowerCase().compareTo( dataContent2.getPath().getFileName().toLowerCase() );
                if ( dataContent.isDirectory() && dataContent2.isDirectory() ) {
                    return fileCompare;
                }

                if ( dataContent.isDirectory() ) {
                    return -1;
                }

                if ( dataContent2.isDirectory() ) {
                    return 1;
                }

                return fileCompare;

            }
        } );

        if ( !path.equals( path.getRoot() ) ) {
            while ( !path.getParent().equals( path.getRoot() ) ) {
                path = path.getParent();
                breadcrumbs.add( paths.convert( path ) );
            }

            reverse( breadcrumbs );
        }
        final org.uberfire.backend.vfs.Path root = paths.convert( path.getRoot() );

        return new NavigatorContent( repositoryService.getRepository( root ).getAlias(), root, breadcrumbs, result );
    }

    @Override
    public List<Repository> listRepositories() {
        return new ArrayList<Repository>( repositoryService.getRepositories() );
    }
}
