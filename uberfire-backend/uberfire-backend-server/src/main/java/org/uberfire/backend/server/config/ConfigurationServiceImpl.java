package org.uberfire.backend.server.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.security.Identity;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final String LAST_MODIFIED_MARKER_FILE = ".lastmodified";
    private static final String MONITOR_DISABLED = "org.kie.sys.repo.monitor.disabled";
    private static final String MONITOR_CHECK_INTERVAL = "org.kie.sys.repo.monitor.interval";
    // mainly for windows as *NIX is based on POSIX but escape always to keep it consistent
    private static final String INVALID_FILENAME_CHARS = "[\\,/,:,*,?,\",<,>,|]";

    @Inject
    @Named("system")
    private Repository systemRepository;

    @Inject
    private ConfigGroupMarshaller marshaller;

    @Inject
    private Identity identity;

    //Cache of ConfigGroups to avoid reloading them from file
    private final Map<ConfigType, List<ConfigGroup>> configuration = new ConcurrentHashMap<ConfigType, List<ConfigGroup>>();
    private long localLastModifiedValue = -1;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    // monitor capabilities
    @Inject
    private Event<SystemRepositoryChangedEvent> changedEvent;
    private ExecutorService executorService;
    private CheckConfigurationUpdates configUpdates;

    @PostConstruct
    public void setup() {
        try {
            ioService.newFileSystem( URI.create( systemRepository.getUri() ),
                                     systemRepository.getEnvironment(),
                                     FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE );
            updateLastModified();
        } catch ( FileSystemAlreadyExistsException e ) {
        }
        // enable monitor by default
        if ( System.getProperty( MONITOR_DISABLED ) == null ) {
            executorService = Executors.newSingleThreadExecutor();
            configUpdates = new CheckConfigurationUpdates();
            executorService.execute( configUpdates );
        }
    }

    @PreDestroy
    public void shutdown() {
        if ( configUpdates != null ) {
            configUpdates.deactivate();
        }
        if ( this.executorService != null ) {
            this.executorService.shutdownNow();
        }
    }

    @Override
    public List<ConfigGroup> getConfiguration( final ConfigType type ) {
        if ( configuration.containsKey( type ) ) {
            return configuration.get( type );
        }
        final List<ConfigGroup> configGroups = new ArrayList<ConfigGroup>();
        final DirectoryStream<Path> foundConfigs = ioService.newDirectoryStream( ioService.get( systemRepository.getUri() ),
                                                                                 new DirectoryStream.Filter<Path>() {
                                                                                     @Override
                                                                                     public boolean accept( final Path entry ) throws IOException {
                                                                                         if ( !Files.isDirectory( entry ) &&
                                                                                                 !entry.getFileName().toString().startsWith( "." ) &&
                                                                                                 entry.getFileName().toString().endsWith( type.getExt() ) ) {
                                                                                             return true;
                                                                                         }
                                                                                         return false;
                                                                                     }
                                                                                 } );
        //Only load and cache if a file was found!
        final Iterator<Path> it = foundConfigs.iterator();
        if ( it.hasNext() ) {
            while ( it.hasNext() ) {
                final String content = ioService.readAllString( it.next() );
                final ConfigGroup configGroup = marshaller.unmarshall( content );
                configGroups.add( configGroup );
            }
            configuration.put( type, configGroups );
        }
        return configGroups;
    }

    @Override
    public boolean addConfiguration( final ConfigGroup configGroup ) {
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );

        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );
        // avoid duplicated writes to not cause cyclic cluster sync
        if ( ioService.exists( filePath ) ) {
            return true;
        }

        final CommentedOption commentedOption = new CommentedOption( getIdentityName(),
                                                                     "Created config " + filePath.getFileName() );
        ioService.write( filePath, marshaller.marshall( configGroup ), commentedOption );

        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove( configGroup.getType() );
        updateLastModified();

        return true;
    }

    @Override
    public boolean updateConfiguration( ConfigGroup configGroup ) {
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );

        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );

        final CommentedOption commentedOption = new CommentedOption( getIdentityName(),
                                                                     "Updated config " + filePath.getFileName() );
        ioService.write( filePath, marshaller.marshall( configGroup ), commentedOption );

        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove( configGroup.getType() );
        updateLastModified();

        return true;
    }

    @Override
    public boolean removeConfiguration( final ConfigGroup configGroup ) {

        //Invalidate cache if an item has been removed; otherwise cached value is stale
        configuration.remove( configGroup.getType() );
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );
        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );

        // avoid duplicated writes to not cause cyclic cluster sync
        if ( !ioService.exists( filePath ) ) {
            return true;
        }
        boolean result = ioService.deleteIfExists( filePath );

        if ( result ) {
            updateLastModified();
        }

        return result;
    }

    protected String getIdentityName() {
        try {
            return identity.getName();
        } catch ( ContextNotActiveException e ) {
            return "unknown";
        }
    }

    protected long getLastModified() {
        final Path lastModifiedPath = ioService.get( systemRepository.getUri() ).resolve( LAST_MODIFIED_MARKER_FILE );

        return ioService.getLastModifiedTime( lastModifiedPath ).toMillis();
    }

    protected void updateLastModified() {
        final Path lastModifiedPath = ioService.get( systemRepository.getUri() ).resolve( LAST_MODIFIED_MARKER_FILE );
        final CommentedOption commentedOption = new CommentedOption( "system", "system repo updated" );

        ioService.write( lastModifiedPath, new Date().toString().getBytes(), commentedOption );

        // update the last value to avoid to be retriggered byt the monitor
        localLastModifiedValue = getLastModified();
    }

    private class CheckConfigurationUpdates implements Runnable {

        private boolean active = true;

        @Override
        public void run() {

            while ( active ) {
                try {
                    long currentValue = getLastModified();
                    if ( currentValue > localLastModifiedValue ) {
                        localLastModifiedValue = currentValue;
                        // invalidate cached values as system repo has changed - for now only for deployments
                        configuration.remove( ConfigType.DEPLOYMENT );
                        changedEvent.fire( new SystemRepositoryChangedEvent() );

                    }

                    Thread.sleep( Long.parseLong( System.getProperty( MONITOR_CHECK_INTERVAL, "2000" ) ) );
                } catch ( Exception e ) {

                }
            }
        }

        public void deactivate() {
            this.active = false;
        }
    }
}
