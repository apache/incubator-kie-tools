package org.uberfire.backend.server.config;

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.FileSystemType;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.StandardOpenOption;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.security.Identity;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService {

    @Inject
    @Named("system")
    private Repository systemRepository;

    @Inject
    private ConfigGroupMarshaller marshaller;

    @Inject
    private Identity identity;

    private IOService ioSystemService = new IOServiceNio2WrapperImpl();
    private Map<ConfigType, List<ConfigGroup>> configuration = new HashMap<ConfigType, List<ConfigGroup>>();

    @PostConstruct
    public void setup() {
        try {
            ioSystemService.newFileSystem( URI.create( systemRepository.getUri() ),
                                           systemRepository.getEnvironment(),
                                           FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException e ) {
            //Do nothing, it already exists
        }
    }

    @Produces
    @Named("ioSystemStrategy")
    public IOService ioSystemService() {
        return ioSystemService;
    }

    @Override
    public List<ConfigGroup> getConfiguration( final ConfigType type ) {
        if ( configuration.containsKey( type ) ) {
            return configuration.get( type );
        }
        final List<ConfigGroup> configGroups = new ArrayList<ConfigGroup>();
        final DirectoryStream<Path> foundConfigs = ioSystemService.newDirectoryStream( ioSystemService.get( systemRepository.getUri() ),
                                                                                       new DirectoryStream.Filter<Path>() {
                                                                                           @Override
                                                                                           public boolean accept( final Path entry ) throws IOException {
                                                                                               if ( !org.kie.commons.java.nio.file.Files.isDirectory( entry ) &&
                                                                                                       entry.getFileName().toString().endsWith( type.getExt() ) ) {
                                                                                                   return true;
                                                                                               }
                                                                                               return false;
                                                                                           }
                                                                                       } );
        final Iterator<Path> it = foundConfigs.iterator();
        while ( it.hasNext() ) {
            final String content = ioSystemService.readAllString( it.next() );
            final ConfigGroup configGroup = marshaller.unmarshall( content );
            configGroups.add( configGroup );
        }
        configuration.put( type,
                           configGroups );
        return configGroups;
    }

    @Override
    public boolean addConfiguration( final ConfigGroup configGroup ) {
        try {
            final Path filePath = ioSystemService.get( systemRepository.getUri() ).resolve( configGroup.getName() + configGroup.getType().getExt() );
            final CommentedOption commentedOption = new CommentedOption( getIdentityName(),
                                                                         "Created config " + filePath.getFileName() );
            final OutputStream outputStream = ioSystemService.newOutputStream( filePath,
                                                                               StandardOpenOption.TRUNCATE_EXISTING,
                                                                               commentedOption );

            final String xml = marshaller.marshall( configGroup );
            outputStream.write( xml.getBytes( "UTF-8" ) );
            outputStream.close();

            return true;

        } catch ( java.io.IOException e ) {
            throw new RuntimeException( "Error when creating asset", e );
        }
    }

    @Override
    public boolean removeConfiguration( final ConfigGroup configGroup ) {
        return ioSystemService.deleteIfExists( ioSystemService.get( systemRepository.getUri() ).resolve( configGroup.getName() + configGroup.getType().getExt() ) );
    }

    protected String getIdentityName() {
        try{
            return identity.getName();
        } catch (ContextNotActiveException e) {
            return "unknown";
        }
    }
}
