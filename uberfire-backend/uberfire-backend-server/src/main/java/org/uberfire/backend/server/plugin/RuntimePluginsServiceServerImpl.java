package org.uberfire.backend.server.plugin;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.Paths;
import org.uberfire.backend.plugin.RuntimePluginsService;

@Service
@ApplicationScoped
public class RuntimePluginsServiceServerImpl implements RuntimePluginsService {

    @Inject
    @Named("uf")
    private ServletContext servletContext;

    @Override
    public Collection<String> listFramworksContent() {
        return directoryContent( "frameworks", "*.js" );
    }

    @Override
    public Collection<String> listPluginsContent() {
        return directoryContent( "plugins", "*.js" );
    }

    @Override
    public String getTemplateContent( String url ) {
        final Path template;
        if ( url.startsWith( "/" ) ) {
            template = Paths.get( URI.create( "file://" + servletContext.getRealPath( "plugins" ) + url ) );
        } else {
            template = Paths.get( URI.create( "file://" + servletContext.getRealPath( "plugins" ) + "/" + url ) );
        }

        if ( Files.isRegularFile( template ) ) {
            return new String( Files.readAllBytes( template ) );
        }
        return "";
    }

    private Collection<String> directoryContent( final String directory,
                                                 final String glob ) {
        final Collection<String> result = new ArrayList<String>();

        final Path pluginsRootPath = Paths.get( URI.create( "file://" + servletContext.getRealPath( directory ) ) );

        if ( Files.isDirectory( pluginsRootPath ) ) {
            final DirectoryStream<Path> stream = Files.newDirectoryStream( pluginsRootPath, glob );

            for ( final Path activeJS : stream ) {
                result.add( new String( Files.readAllBytes( activeJS ) ) );
            }
        }

        return result;
    }

}
