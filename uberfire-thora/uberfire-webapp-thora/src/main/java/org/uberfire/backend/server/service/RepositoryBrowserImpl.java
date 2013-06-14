package org.uberfire.backend.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.shared.browser.FileContent;
import org.uberfire.shared.browser.RepositoryBrowser;
import org.uberfire.shared.browser.ResultListContent;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class RepositoryBrowserImpl implements RepositoryBrowser {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public ResultListContent listContent( final org.uberfire.backend.vfs.Path _path ) {
        final ArrayList<FileContent> result = new ArrayList<FileContent>();
        final ArrayList<org.uberfire.backend.vfs.Path> breadcrumbs = new ArrayList<org.uberfire.backend.vfs.Path>();

        Path path = paths.convert( _path );
        final DirectoryStream<Path> stream = ioService.newDirectoryStream( path );

        for ( final Path activePath : stream ) {
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( activePath, VersionAttributeView.class );
            int index = versionAttributeView.readAttributes().history().records().size() - 1;

            final String authorEmail = versionAttributeView.readAttributes().history().records().get( index ).email();
            final String author = versionAttributeView.readAttributes().history().records().get( index ).author();
            final String comment = versionAttributeView.readAttributes().history().records().get( index ).comment();

            result.add( new FileContent( Files.isDirectory( activePath ), comment, author, authorEmail, new Date( Files.getLastModifiedTime( activePath ).toMillis() ).toLocaleString(), paths.convert( activePath ) ) );
        }

        sort( result, new Comparator<FileContent>() {
            @Override
            public int compare( final FileContent fileContent,
                                final FileContent fileContent2 ) {

                int fileCompare = fileContent.getPath().getFileName().toLowerCase().compareTo( fileContent2.getPath().getFileName().toLowerCase() );
                if ( fileContent.isDirectory() && fileContent2.isDirectory() ) {
                    return fileCompare;
                }

                if ( fileContent.isDirectory() ) {
                    return -1;
                }

                if ( fileContent2.isDirectory() ) {
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

        return new ResultListContent( breadcrumbs, result );
    }
}
