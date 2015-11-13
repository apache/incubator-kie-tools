/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.backend;

import java.io.StringWriter;
import java.net.URI;
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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.editor.commons.backend.validation.DefaultFileNameValidator;
import org.uberfire.ext.plugin.event.MediaDeleted;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.DynamicMenuItem;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.Language;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.ext.plugin.type.TypeConverterUtil;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;

@Service
@ApplicationScoped
public class PluginServicesImpl implements PluginServices {

    private static final String MENU_ITEM_DELIMITER = " / ";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("MediaServletURI")
    private Instance<MediaServletURI> mediaServletURI;

    @Inject
    private transient SessionInfo sessionInfo;

    @Inject
    private Event<PluginAdded> pluginAddedEvent;

    @Inject
    private Event<PluginDeleted> pluginDeletedEvent;

    @Inject
    private Event<PluginSaved> pluginSavedEvent;

    @Inject
    private Event<PluginRenamed> pluginRenamedEvent;

    @Inject
    private Event<MediaDeleted> mediaDeletedEvent;

    @Inject
    private DefaultFileNameValidator defaultFileNameValidator;

    @Inject
    private User identity;

    protected Gson gson;

    private FileSystem fileSystem;
    private Path root;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            fileSystem = ioService.newFileSystem( URI.create( "default://plugins" ),
                                                  new HashMap<String, Object>() {{
                                                      put( "init", Boolean.TRUE );
                                                      put( "internal", Boolean.TRUE );
                                                  }} );
        } catch ( FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( "default://plugins" ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public String getMediaServletURI() {
        return mediaServletURI.get().getURI();
    }

    @Override
    public Collection<RuntimePlugin> listRuntimePlugins() {
        final Collection<RuntimePlugin> runtimePlugins = new ArrayList<RuntimePlugin>();
        final Set<Framework> frameworks = new HashSet<Framework>();

        if ( ioService.exists( root ) ) {
            walkFileTree( checkNotNull( "root", root ),
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

        if ( ioService.exists( root ) ) {
            walkFileTree( checkNotNull( "root", root ),
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
        checkCondition( "valid plugin name", defaultFileNameValidator.isValid( pluginName ) );

        final Path pluginRoot = getPluginPath( pluginName );
        if ( ioService.exists( pluginRoot ) ) {
            throw new PluginAlreadyExists();
        }

        final Path pluginPath;

        try {
            ioService.startBatch( fileSystem );
            pluginPath = pluginRoot.resolve( type.toString().toLowerCase() + ".plugin" );
            updatePlugin( pluginPath,
                          pluginName,
                          type,
                          true );
        } finally {
            ioService.endBatch();
        }

        return new Plugin( pluginName,
                           type,
                           convert( pluginPath ) );
    }

    private void updatePlugin( final Path pluginPath,
                               final String pluginName,
                               final PluginType type,
                               final boolean isNewPlugIn ) {
        try {
            ioService.startBatch( fileSystem );
            ioService.write( pluginPath,
                             new Date().toString() );
        } finally {
            ioService.endBatch();
        }
        if ( isNewPlugIn ) {
            pluginAddedEvent.fire( new PluginAdded( new Plugin( pluginName,
                                                                type,
                                                                convert( pluginPath ) ),
                                                    sessionInfo ) );
        } else {
            pluginSavedEvent.fire( new PluginSaved( pluginName,
                                                    type,
                                                    sessionInfo ) );
        }
    }

    @Override
    public PluginContent getPluginContent( final org.uberfire.backend.vfs.Path path ) {
        final String pluginName = convert( path ).getParent().getFileName().toString();
        return new PluginContent( pluginName,
                                  TypeConverterUtil.fromPath( path ),
                                  path,
                                  loadTemplate( pluginName ),
                                  loadCss( pluginName ),
                                  loadCodeMap( pluginName ),
                                  loadFramework( pluginName ),
                                  Language.JAVASCRIPT,
                                  loadMediaLibrary( pluginName ) );
    }

    @Override
    public org.uberfire.backend.vfs.Path save( final PluginSimpleContent plugin,
                                               final String commitMessage ) {

        final Path pluginPath = convert( plugin.getPath() );
        final boolean isNewPlugin = !ioService.exists( pluginPath );

        try {
            ioService.startBatch( fileSystem,
                                  commentedOption( commitMessage ) );

            saveCodeMap( plugin.getName(),
                         plugin.getCodeMap() );

            if ( plugin.getTemplate() != null ) {
                ioService.write( getTemplatePath( getPluginPath( plugin.getName() ) ),
                                 plugin.getTemplate() );
            }

            if ( plugin.getCss() != null ) {
                ioService.write( getCssPath( getPluginPath( plugin.getName() ) ),
                                 plugin.getCss() );
            }

            clearDirectory( getPluginPath( plugin.getName() ).resolve( "dependencies" ) );

            if ( plugin.getFrameworks() != null && !plugin.getFrameworks().isEmpty() ) {
                final Framework framework = plugin.getFrameworks().iterator().next();
                ioService.write( getDependencyPath( getPluginPath( plugin.getName() ), framework ), "--" );
            }

            createRegistry( plugin );

            updatePlugin( pluginPath,
                          plugin.getName(),
                          plugin.getType(),
                          isNewPlugin );

        } finally {
            ioService.endBatch();
        }

        return plugin.getPath();
    }

    private void clearDirectory( Path directory ) {
        if ( ioService.exists( directory ) ) {
            for ( Path path : ioService.newDirectoryStream( directory ) ) {
                boolean b = ioService.deleteIfExists( path );
            }
        }
    }

    private Path getDependencyPath( final Path pluginPath,
                                    final Framework framework ) {
        return pluginPath.resolve( "dependencies" ).resolve( framework.toString() + ".dependency" );
    }

    private void createRegistry( final PluginSimpleContent plugin ) {
        final Path path = getPluginPath( plugin.getName() );

        final String registry = new JSRegistry().convertToJSRegistry( plugin );

        ioService.write( path.resolve( plugin.getName() + ".registry.js" ),
                         registry );
    }


    private void saveCodeMap( final String pluginName,
                              final Map<CodeType, String> codeMap ) {
        final Path rootPlugin = getPluginPath( pluginName );
        for ( final Map.Entry<CodeType, String> entry : codeMap.entrySet() ) {
            final Path codePath = getCodePath( rootPlugin,
                                               entry.getKey() );
            ioService.write( codePath,
                             entry.getValue() );
        }
    }

    private Map<CodeType, String> loadCodeMap( final String pluginName ) {
        try {
            final Path rootPlugin = getPluginPath( pluginName );
            final DirectoryStream<Path> stream = ioService.newDirectoryStream( getCodeRoot( rootPlugin ),
                                                                               new DirectoryStream.Filter<Path>() {
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
                result.add( new Media( getMediaServletURI() + pluginName + "/media/" + path.getFileName(),
                                       convert( path ) ) );
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
        return root.resolve( name );
    }

    @Override
    public void delete( final org.uberfire.backend.vfs.Path path,
                        final String comment ) {
        final Plugin plugin = getPluginContent( path );
        final Path pluginPath = convert( plugin.getPath() );
        if ( ioService.exists( pluginPath ) ) {

            try {
                ioService.startBatch( fileSystem,
                                      commentedOption( comment ) );
                ioService.deleteIfExists( pluginPath.getParent(),
                                          StandardDeleteOption.NON_EMPTY_DIRECTORIES );

            } finally {
                ioService.endBatch();
            }

            pluginDeletedEvent.fire( new PluginDeleted( plugin.getName(),
                                                        plugin.getType(),
                                                        sessionInfo ) );
        }
    }

    @Override
    public org.uberfire.backend.vfs.Path copy( final org.uberfire.backend.vfs.Path path,
                                               final String newName,
                                               final String comment ) {

        final Path newPath = getPluginPath( newName );
        if ( ioService.exists( newPath ) ) {
            throw new RuntimeException( new FileAlreadyExistsException( newPath.toString() ) );
        }

        try {
            ioService.startBatch( fileSystem,
                                  commentedOption( comment ) );
            ioService.copy( convert( path ).getParent(),
                            newPath );

        } finally {
            ioService.endBatch();
        }

        final org.uberfire.backend.vfs.Path result = convert( newPath.resolve( path.getFileName() ) );

        pluginAddedEvent.fire( new PluginAdded( getPluginContent( convert( newPath.resolve( path.getFileName() ) ) ),
                                                sessionInfo ) );

        return result;
    }

    @Override
    public org.uberfire.backend.vfs.Path rename( final org.uberfire.backend.vfs.Path path,
                                                 final String newName,
                                                 final String comment ) {
        final Path newPath = getPluginPath( newName );
        if ( ioService.exists( newPath ) ) {
            throw new RuntimeException( new FileAlreadyExistsException( newPath.toString() ) );
        }

        try {
            ioService.startBatch( fileSystem,
                                  commentedOption( comment ) );
            ioService.move( convert( path ).getParent(),
                            newPath );

        } finally {
            ioService.endBatch();
        }

        final org.uberfire.backend.vfs.Path result = convert( newPath.resolve( path.getFileName() ) );

        final String oldPluginName = convert( path ).getParent().getFileName().toString();

        pluginRenamedEvent.fire( new PluginRenamed( oldPluginName,
                                                    getPluginContent( convert( newPath.resolve( path.getFileName() ) ) ),
                                                    sessionInfo ) );

        return result;
    }

    private CommentedOption commentedOption( final String comment ) {
        return new CommentedOption( sessionInfo != null ? sessionInfo.getId() : "--",
                                    identity.getIdentifier(),
                                    null,
                                    comment );
    }

    @Override
    public void deleteMedia( final Media media ) {
        final Path mediaPath = convert( media.getPath() );

        try {
            ioService.startBatch( fileSystem );
            ioService.delete( mediaPath );

        } finally {
            ioService.endBatch();
        }

        mediaDeletedEvent.fire( new MediaDeleted( mediaPath.getParent().getParent().getFileName().toString(),
                                                  media ) );
    }

    @Override
    public DynamicMenu getDynamicMenuContent( org.uberfire.backend.vfs.Path path ) {
        final String pluginName = convert( path ).getParent().getFileName().toString();
        return new DynamicMenu( pluginName,
                                TypeConverterUtil.fromPath( path ),
                                path,
                                loadMenuItems( pluginName ) );
    }

    @Override
    public LayoutEditorModel getLayoutEditor( org.uberfire.backend.vfs.Path path,
                                              PluginType pluginType ) {
        final String pluginName = convert( path ).getParent().getFileName().toString();

        return loadLayoutEditor( pluginName,
                                 path, pluginType );
    }

    private LayoutEditorModel loadLayoutEditor( String pluginName,
                                                org.uberfire.backend.vfs.Path path,
                                                PluginType type ) {
        final Path path1 = getLayoutEditorPath( getPluginPath( pluginName ), type.toString().toLowerCase() );
        if ( ioService.exists( path1 ) ) {
            String fileContent = ioService.readAllString( path1 );

            return new LayoutEditorModel( pluginName,
                                          PluginType.PERSPECTIVE_LAYOUT,
                                          path,
                                          fileContent );
        }

        return new LayoutEditorModel().emptyLayout();
    }

    @Override
    public org.uberfire.backend.vfs.Path saveMenu( final DynamicMenu plugin,
                                                   final String commitMessage ) {
        final Path pluginPath = convert( plugin.getPath() );
        final boolean isNewPlugin = !ioService.exists( pluginPath );

        try {
            ioService.startBatch( fileSystem,
                                  commentedOption( commitMessage ) );

            final Path menuItemsPath = getMenuItemsPath( getPluginPath( plugin.getName() ) );
            final StringBuilder sb = new StringBuilder();
            for ( DynamicMenuItem item : plugin.getMenuItems() ) {
                sb.append( item.getActivityId() ).append( MENU_ITEM_DELIMITER ).append( item.getMenuLabel() ).append( "\n" );
            }
            ioService.write( menuItemsPath,
                             sb.toString() );

            updatePlugin( pluginPath,
                          plugin.getName(),
                          plugin.getType(),
                          isNewPlugin );

        } finally {
            ioService.endBatch();
        }

        return plugin.getPath();
    }

    @Override
    public org.uberfire.backend.vfs.Path saveLayout( LayoutEditorModel plugin,
                                                     String commitMessage ) {
        final Path pluginPath = convert( plugin.getPath() );
        final boolean isNewPlugin = !ioService.exists( pluginPath );

        try {
            ioService.startBatch( fileSystem, commentedOption( commitMessage ) );

            final Path itemsPath = getLayoutEditorPath( getPluginPath( plugin.getName() ), plugin.getType().toString().toLowerCase() );

            ioService.write( itemsPath,
                             plugin.getLayoutEditorModel() );

            updatePlugin( pluginPath,
                          plugin.getName(),
                          plugin.getType(),
                          isNewPlugin );
        } finally {
            ioService.endBatch();
        }
        return plugin.getPath();
    }

    private Path getLayoutEditorPath( final Path rootPlugin,
                                      final String type ) {
        return rootPlugin.resolve( type );
    }

    @Override
    public Collection<DynamicMenu> listDynamicMenus() {
        final Collection<DynamicMenu> result = new ArrayList<DynamicMenu>();

        if ( ioService.exists( root ) ) {
            walkFileTree( checkNotNull( "root", root ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( file.getFileName().toString().equalsIgnoreCase( "info.dynamic" ) && attrs.isRegularFile() ) {
                                          final String pluginName = file.getParent().getFileName().toString();
                                          result.add( new DynamicMenu( pluginName,
                                                                       PluginType.DYNAMIC_MENU,
                                                                       convert( file.getParent() ),
                                                                       loadMenuItems( pluginName ) ) );
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
    public Collection<LayoutEditorModel> listLayoutEditor( final PluginType pluginType ) {
        final Collection<LayoutEditorModel> result = new ArrayList<LayoutEditorModel>();

        if ( ioService.exists( root ) ) {
            walkFileTree( checkNotNull( "root", root ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );
                                      if ( file.getFileName().toString().equalsIgnoreCase( pluginType.toString().toLowerCase() ) && attrs.isRegularFile() ) {
                                          final LayoutEditorModel layoutEditorModel = getLayoutEditor( convert( file ), pluginType );
                                          result.add( layoutEditorModel );
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
                final String[] items = s.split( MENU_ITEM_DELIMITER );
                if ( items.length == 2 ) {
                    result.add( new DynamicMenuItem( items[ 0 ],
                                                     items[ 1 ] ) );
                }
            }
        }
        return result;
    }

    private Path getMenuItemsPath( final Path rootPlugin ) {
        return rootPlugin.resolve( "info.dynamic" );
    }

}
