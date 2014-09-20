package org.kie.uberfire.plugin.backend;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;
import org.kie.uberfire.plugin.exception.PluginAlreadyExists;
import org.kie.uberfire.plugin.event.MediaDeleted;
import org.kie.uberfire.plugin.event.PluginAdded;
import org.kie.uberfire.plugin.event.PluginDeleted;
import org.kie.uberfire.plugin.event.PluginSaved;
import org.kie.uberfire.plugin.model.CodeType;
import org.kie.uberfire.plugin.model.DynamicMenu;
import org.kie.uberfire.plugin.model.DynamicMenuItem;
import org.kie.uberfire.plugin.model.Framework;
import org.kie.uberfire.plugin.model.Language;
import org.kie.uberfire.plugin.model.Media;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginContent;
import org.kie.uberfire.plugin.model.PluginSimpleContent;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.model.RuntimePlugin;
import org.kie.uberfire.plugin.service.PluginServices;
import org.kie.uberfire.plugin.type.TypeConverterUtil;

import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;

@Service
@ApplicationScoped
public class PluginServicesImpl implements PluginServices {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    @Named("MediaServletURI")
    private MediaServletURI mediaServletURI;

    @Inject
    private transient SessionInfo sessionInfo;

    @Inject
    private Event<PluginAdded> pluginAddedEvent;

    @Inject
    private Event<PluginDeleted> pluginDeletedEvent;

    @Inject
    private Event<PluginSaved> pluginSavedEvent;

    @Inject
    private Event<MediaDeleted> mediaDeletedEvent;

    private Gson gson;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String getMediaServletURI() {
        return mediaServletURI.getURI();
    }

    @Override
    public Collection<RuntimePlugin> listRuntimePlugins() {
        final Collection<RuntimePlugin> runtimePlugins = new ArrayList<RuntimePlugin>();
        final Path plugins = fileSystem.getPath( "plugins", "/" );
        final Set<Framework> frameworks = new HashSet<Framework>();

        if ( ioService.exists( plugins ) ) {
            walkFileTree( checkNotNull( "root", plugins ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( file.getFileName().toString().endsWith( ".registry.js" ) && attrs.isRegularFile() ) {
                                          final String pluginName = file.getParent().getFileName().toString();
                                          frameworks.addAll( loadFramework( pluginName ) );
                                          runtimePlugins.add( new RuntimePlugin( loadCss( pluginName ), ioService.readAllString( file ) ) );
                                      }
                                  } catch ( final Exception ex ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
        }

        final Collection<RuntimePlugin> result = new ArrayList<RuntimePlugin>( frameworks.size() + runtimePlugins.size() );
        for ( final Framework framework : frameworks ) {
            try {
                final StringWriter writer = new StringWriter();
                IOUtils.copy( getClass().getClassLoader().getResourceAsStream( "/frameworks/" + framework.toString().toLowerCase() + ".dependency" ), writer );
                result.add( new RuntimePlugin( "", writer.toString() ) );
            } catch ( final Exception ignored ) {
            }
        }
        result.addAll( runtimePlugins );

        return result;
    }

    @Override
    public Collection<Plugin> listPlugins() {
        final Collection<Plugin> result = new ArrayList<Plugin>();
        final Path plugins = fileSystem.getPath( "plugins", "/" );

        if ( ioService.exists( plugins ) ) {
            walkFileTree( checkNotNull( "root", plugins ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( file.getFileName().toString().endsWith( ".plugin" ) && attrs.isRegularFile() ) {
                                          final org.uberfire.backend.vfs.Path path = convert( file );
                                          result.add( new Plugin( file.getParent().getFileName().toString(), TypeConverterUtil.fromPath( path ), path ) );
                                      }
                                  } catch ( final Exception ex ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
        }

        return result;
    }

    @Override
    public Plugin createNewPlugin( final String pluginName,
                                   final PluginType type ) {
        checkNotEmpty( "pluginName", pluginName );

        final Path pluginRoot = getPluginPath( pluginName );
        if ( ioService.exists( pluginRoot ) ) {
            throw new PluginAlreadyExists();
        }

        final Path pluginPath = ioService.createFile( pluginRoot.resolve( type.toString().toLowerCase() + ".plugin" ) );

        updatePlugin( pluginPath, pluginName, type, true );

        return new Plugin( pluginName, type, convert( pluginPath ) );
    }

    private void updatePlugin( final Path pluginPath,
                               final String pluginName,
                               final PluginType type,
                               final boolean isNewPlugIn ) {
        ioService.write( pluginPath, new Date().toString() );
        if ( isNewPlugIn ) {
            pluginAddedEvent.fire( new PluginAdded( new Plugin( pluginName, type, convert( pluginPath ) ), sessionInfo ) );
        } else {
            pluginSavedEvent.fire( new PluginSaved( pluginName, type, sessionInfo ) );
        }
    }

    @Override
    public PluginContent getPluginContent( final org.uberfire.backend.vfs.Path path ) {
        final String pluginName = convert( path ).getParent().getFileName().toString();
        return new PluginContent( pluginName, TypeConverterUtil.fromPath( path ), path, loadTemplate( pluginName ), loadCss( pluginName ), loadCodeMap( pluginName ), loadFramework( pluginName ), Language.JAVASCRIPT, loadMediaLibrary( pluginName ) );
    }

    @Override
    public void save( final PluginSimpleContent plugin ) {
        final Path pluginPath = convert( plugin.getPath() );
        final boolean isNewPlugin = !ioService.exists( pluginPath );

        if ( isNewPlugin ) {
            ioService.createFile( getPluginPath( plugin.getName() ).resolve( plugin.getType().toString().toLowerCase() + ".plugin" ) );
        }

        saveCodeMap( plugin.getName(), plugin.getCodeMap() );

        if ( plugin.getTemplate() != null ) {
            ioService.write( getTemplatePath( getPluginPath( plugin.getName() ) ), plugin.getTemplate() );
        }

        if ( plugin.getCss() != null ) {
            ioService.write( getCssPath( getPluginPath( plugin.getName() ) ), plugin.getCss() );
        }

        try {
            if ( plugin.getFrameworks() != null && !plugin.getFrameworks().isEmpty() ) {
                final Framework fm = plugin.getFrameworks().iterator().next();
                ioService.createFile( getDependencyPath( getPluginPath( plugin.getName() ), fm ) );
            }
        } catch ( final FileAlreadyExistsException ex ) {

        }

        createRegistry( plugin );

        updatePlugin( pluginPath, plugin.getName(), plugin.getType(), isNewPlugin );
    }

    private Path getDependencyPath( final Path pluginPath,
                                    final Framework framework ) {
        return pluginPath.resolve( "dependencies" ).resolve( framework.toString() + ".dependency" );
    }

    private void createRegistry( final PluginSimpleContent plugin ) {
        final Path path = getPluginPath( plugin.getName() );

        final StringBuilder sb = new StringBuilder();

        if ( plugin.getCodeMap().containsKey( CodeType.MAIN ) ) {
            sb.append( plugin.getCodeMap().get( CodeType.MAIN ) );
        }

        if ( plugin.getType().equals( PluginType.SCREEN ) ) {
            sb.append( "$registerPlugin({" );
        } else if ( plugin.getType().equals( PluginType.SPLASH ) ) {
            sb.append( "$registerSplashScreen({" );
        } else if ( plugin.getType().equals( PluginType.EDITOR ) ) {
            sb.append( "$registerEditor({" );
        }
        if ( plugin.getType().equals( PluginType.PERSPECTIVE ) ) {
            sb.append( "$registerPerspective({" );
        }

        sb.append( "id:" ).append( '"' ).append( plugin.getName() ).append( '"' ).append( "," );

        if ( plugin.getCodeMap().size() > 1 ) {
            for ( final Map.Entry<CodeType, String> entry : plugin.getCodeMap().entrySet() ) {
                if ( !entry.getKey().equals( CodeType.MAIN ) ) {
                    sb.append( entry.getKey().toString().toLowerCase() ).append( ": " );
                    sb.append( entry.getValue() ).append( "," );
                }
            }
        } else {
            sb.append( "," );
        }

        if ( plugin.getFrameworks() != null && !plugin.getFrameworks().isEmpty() ) {
            final Framework fm = plugin.getFrameworks().iterator().next();
            sb.append( "type: " ).append( '"' ).append( fm.getType() ).append( '"' ).append( ',' );
        }

        if ( !plugin.getType().equals( PluginType.PERSPECTIVE ) ) {
            sb.append( "template: " );

            gson.toJson( plugin.getTemplate(), sb );
        } else {
            sb.append( "view: {" ).append( plugin.getTemplate() ).append( "}" );
        }

        sb.append( "});" );

        ioService.write( path.resolve( plugin.getName() + ".registry.js" ), sb.toString() );
    }

    private void saveCodeMap( final String pluginName,
                              final Map<CodeType, String> codeMap ) {
        final Path rootPlugin = getPluginPath( pluginName );
        for ( final Map.Entry<CodeType, String> entry : codeMap.entrySet() ) {
            final Path codePath = getCodePath( rootPlugin, entry.getKey() );
            ioService.write( codePath, entry.getValue() );
        }
    }

    private Map<CodeType, String> loadCodeMap( final String pluginName ) {
        try {
            final Path rootPlugin = getPluginPath( pluginName );
            final DirectoryStream<Path> stream = ioService.newDirectoryStream( getCodeRoot( rootPlugin ), new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept( final Path entry ) throws IOException {
                    return entry.getFileName().toString().endsWith( ".code" );
                }
            } );

            final Map<CodeType, String> result = new HashMap<CodeType, String>();

            for ( final Path path : stream ) {
                final CodeType type = getCodeType( path );
                if ( type != null ) {
                    result.put( type, ioService.readAllString( path ) );
                }
            }

            return result;
        } catch ( final NotDirectoryException exception ) {
            return Collections.emptyMap();
        }
    }

    private Set<Media> loadMediaLibrary( final String pluginName ) {
        try {
            final Path rootPlugin = getPluginPath( pluginName );
            final DirectoryStream<Path> stream = ioService.newDirectoryStream( getMediaRoot( rootPlugin ) );

            final Set<Media> result = new HashSet<Media>();

            for ( final Path path : stream ) {
                result.add( new Media( "/" + getMediaServletURI() + pluginName + "/media/" + path.getFileName(), convert( path ) ) );
            }

            return result;
        } catch ( final NotDirectoryException exception ) {
            return Collections.emptySet();
        }
    }

    private String loadTemplate( final String pluginName ) {
        final Path template = getTemplatePath( getPluginPath( pluginName ) );
        if ( ioService.exists( template ) ) {
            return ioService.readAllString( template );
        }
        return "";
    }

    private String loadCss( final String pluginName ) {
        final Path css = getCssPath( getPluginPath( pluginName ) );
        if ( ioService.exists( css ) ) {
            return ioService.readAllString( css );
        }
        return "";
    }

    private Set<Framework> loadFramework( final String pluginName ) {
        try {
            final Set<Framework> result = new HashSet<Framework>();
            final DirectoryStream<Path> stream = ioService.newDirectoryStream( getPluginPath( pluginName ).resolve( "dependencies" ) );

            for ( final Path path : stream ) {
                try {
                    result.add( Framework.valueOf( path.getFileName().toString().replace( ".dependency", "" ).toUpperCase() ) );
                } catch ( final Exception ignored ) {
                }
            }

            return result;
        } catch ( final NotDirectoryException exception ) {
            return Collections.emptySet();
        }
    }

    private Path getTemplatePath( final Path rootPlugin ) {
        return rootPlugin.resolve( "template.html" );
    }

    private Path getCssPath( final Path rootPlugin ) {
        return rootPlugin.resolve( "css" ).resolve( "style.css" );
    }

    private Path getCodePath( final Path rootPlugin,
                              final CodeType codeType ) {
        return getCodeRoot( rootPlugin ).resolve( codeType.toString().toLowerCase() + ".code" );
    }

    private CodeType getCodeType( final Path path ) {
        try {
            return CodeType.valueOf( path.getFileName().toString().replace( ".code", "" ).toUpperCase() );
        } catch ( final Exception ignored ) {
        }
        return null;
    }

    private Path getCodeRoot( final Path rootPlugin ) {
        return rootPlugin.resolve( "code" );
    }

    private Path getMediaRoot( final Path rootPlugin ) {
        return rootPlugin.resolve( "media" );
    }

    private Path getPluginPath( final String name ) {
        return fileSystem.getPath( "plugins", "/" + name );
    }

    @Override
    public void delete( final Plugin plugin ) {
        final Path pluginPath = convert( plugin.getPath() );
        if ( ioService.exists( pluginPath ) ) {
            ioService.delete( pluginPath.getParent(), StandardDeleteOption.NON_EMPTY_DIRECTORIES );
            pluginDeletedEvent.fire( new PluginDeleted( plugin.getName(), plugin.getType(), sessionInfo ) );
        }
    }

    @Override
    public void deleteMedia( final Media media ) {
        final Path mediaPath = convert( media.getPath() );
        ioService.delete( mediaPath );
        mediaDeletedEvent.fire( new MediaDeleted( mediaPath.getParent().getParent().getFileName().toString(), media ) );
    }

    @Override
    public DynamicMenu getDynamicMenuContent( org.uberfire.backend.vfs.Path path ) {
        final String pluginName = convert( path ).getParent().getFileName().toString();
        return new DynamicMenu( pluginName, TypeConverterUtil.fromPath( path ), path, loadMenuItems( pluginName ) );
    }

    @Override
    public void save( final DynamicMenu plugin ) {
        final Path pluginPath = convert( plugin.getPath() );
        final boolean isNewPlugin = !ioService.exists( pluginPath );

        if ( isNewPlugin ) {
            ioService.createFile( getPluginPath( plugin.getName() ).resolve( plugin.getType().toString().toLowerCase() + ".plugin" ) );
        }

        final Path menuItemsPath = getMenuItemsPath( getPluginPath( plugin.getName() ) );
        final StringBuilder sb = new StringBuilder();
        for ( DynamicMenuItem item : plugin.getMenuItems() ) {
            sb.append( item.getActivityId() ).append( " / " ).append( item.getMenuLabel() ).append( "\n" );
        }
        ioService.write( menuItemsPath, sb.toString() );

        updatePlugin( pluginPath, plugin.getName(), plugin.getType(), isNewPlugin );
    }

    @Override
    public Collection<DynamicMenu> listDynamicMenus() {
        final Collection<DynamicMenu> result = new ArrayList<DynamicMenu>();
        final Path plugins = fileSystem.getPath( "plugins", "/" );

        if ( ioService.exists( plugins ) ) {
            walkFileTree( checkNotNull( "root", plugins ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( file.getFileName().toString().equalsIgnoreCase( "info.dynamic" ) && attrs.isRegularFile() ) {
                                          final String pluginName = file.getParent().getFileName().toString();
                                          result.add( new DynamicMenu( pluginName, PluginType.DYNAMIC_MENU, convert( file.getParent() ), loadMenuItems( pluginName ) ) );
                                      }
                                  } catch ( final Exception ex ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
        }

        return result;
    }

    private Collection<DynamicMenuItem> loadMenuItems( String pluginName ) {
        final Collection<DynamicMenuItem> result = new ArrayList<DynamicMenuItem>();
        final Path menuItemsPath = getMenuItemsPath( getPluginPath( pluginName ) );
        if ( ioService.exists( menuItemsPath ) ) {
            final List<String> value = ioService.readAllLines( menuItemsPath );
            for ( final String s : value ) {
                final String[] items = s.split( "/" );
                if ( items.length == 2 ) {
                    result.add( new DynamicMenuItem( items[ 0 ].trim(), items[ 1 ].trim() ) );
                }
            }
        }
        return result;
    }

    private Path getMenuItemsPath( final Path rootPlugin ) {
        return rootPlugin.resolve( "info.dynamic" );
    }

}
