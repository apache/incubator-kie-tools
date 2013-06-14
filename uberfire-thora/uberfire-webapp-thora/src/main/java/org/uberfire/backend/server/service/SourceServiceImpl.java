package org.uberfire.backend.server.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.shared.source.PathContentUpdated;
import org.uberfire.shared.source.SourceContent;
import org.uberfire.shared.source.SourceLinedContent;
import org.uberfire.shared.source.SourceService;

@Service
@ApplicationScoped
public class SourceServiceImpl implements SourceService {

    private static final XStream xs = new XStream();

    @Inject
    private UserActionsService userServices;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Event<PathContentUpdated> contentUpdatedEvent;

    @Override
    public SourceLinedContent getLinedContent( final org.uberfire.backend.vfs.Path _path ) {
        final Path path = paths.convert( _path );
        final List<String> fileContent = ioService.readAllLines( path, Charset.defaultCharset() );

        return new SourceLinedContent( fileContent, breadcrumb( path ), paths.convert( path.getParent() ) );
    }

    @Override
    public SourceContent getContent( final org.uberfire.backend.vfs.Path _path,
                                     final String fileName ) {
        final Path path = paths.convert( _path ).resolve( fileName );

        String fileContent;
        try {
            fileContent = ioService.readAllString( path, Charset.defaultCharset() );
        } catch ( final Exception ex ) {
            fileContent = null;
        }

        return new SourceContent( fileContent, breadcrumb( path ) );
    }

    @Override
    public void commit( final String repo,
                        final org.uberfire.backend.vfs.Path path,
                        final String fileName,
                        final String content,
                        final String userName,
                        final String email,
                        final String commitSummary,
                        final String commitDescription ) {
        final String summary = commitSummary.trim();
        final String desc = commitDescription.trim();

        final StringBuilder message = new StringBuilder( summary );
        if ( !desc.isEmpty() ) {
            message.append( "\n" + desc );
        }

        final Path realPath = paths.convert( path ).resolve( fileName );

        ioService.write( realPath, content, new CommentedOption( userName, email, message.toString() ) );

        userServices.storeLastContrib( userName, repo );

        contentUpdatedEvent.fire( new PathContentUpdated( paths.convert( realPath ) ) );
    }

    private List<String> breadcrumb( final Path path ) {
        final List<String> breadcrumb = new ArrayList<String>( path.getNameCount() );
        for ( final Path activePart : path ) {
            breadcrumb.add( activePart.getFileName().toString() );
        }
        return breadcrumb;
    }
}
