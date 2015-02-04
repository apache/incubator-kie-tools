package org.uberfire.ext.apps.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.apps.api.AppsPersistenceAPI;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.model.PerspectiveEditorModel;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardDeleteOption;

@Service
@ApplicationScoped
public class AppsPersistenceImpl implements AppsPersistenceAPI {

    public static final String HOME_DIR = ".app_dir/home";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    PluginServices pluginServices;

    private FileSystem fileSystem;
    private Path root;

    @PostConstruct
    public void setup() {
        try {
            fileSystem = ioService.newFileSystem( URI.create( "default://plugins" ),
                                                  new HashMap<String, Object>() {{
                                                      put( "init", Boolean.TRUE );
                                                      put( "internal", Boolean.TRUE );
                                                  }} );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( "default://plugins" ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public Directory getRootDirectory() {

        final Map<String, List<String>> tagMap = generateTagMap();

        final Directory root = buildDirectories( tagMap );

        return root;
    }

    private Map<String, List<String>> generateTagMap() {

        Map<String, List<String>> tagsMap = new HashMap<String, List<String>>();
        final Collection<PerspectiveEditorModel> perspectiveEditors = pluginServices.listPerspectiveEditor();
        for ( PerspectiveEditorModel perspectiveEditorModel : perspectiveEditors ) {
            final PerspectiveEditor perspectiveEditor = perspectiveEditorModel.getPerspectiveModel();
            //PerspectiveEditor is null on legacy PerspectiveLayout definitions
            if ( perspectiveEditor != null ) {
                for ( String tag : perspectiveEditor.getTags() ) {
                    List<String> perspectives = tagsMap.get( tag.toUpperCase() );
                    if ( perspectives == null ) {
                        perspectives = new ArrayList<String>();
                    }
                    perspectives.add( perspectiveEditor.getName() );
                    tagsMap.put( tag.toUpperCase(), perspectives );
                }
            }
        }

        return tagsMap;
    }

    private Directory buildDirectories( Map<String, List<String>> tagMap ) {
        Path homeDir = getHomeDir();

        Directory root = new Directory( homeDir.getFileName().toString(), homeDir.toString(), homeDir.toUri().toString(), tagMap );

        root.addChildDirectories( extractAllChildDirectories( root, homeDir ) );

        return root;
    }

    private List<Directory> extractAllChildDirectories( Directory parent,
                                                        Path dir ) {

        List<Directory> childs = new ArrayList<Directory>();

        if ( ioService.exists( dir ) && Files.isDirectory( dir ) ) {
            final DirectoryStream<Path> paths = ioService.newDirectoryStream( dir );
            for ( Path childPath : paths ) {
                if ( Files.isDirectory( childPath ) ) {
                    final Directory child = getDirectory( childPath.getFileName().toString(), childPath.toString(), childPath.toUri().toString(), parent );
                    final List<Directory> childsOfChilds = extractAllChildDirectories( child, childPath );
                    child.addChildDirectories( childsOfChilds );
                    childs.add( child );
                }
            }
        }
        return childs;
    }

    private Directory getDirectory( String name,
                                    String fullpath,
                                    String uri,
                                    Directory parent ) {
        return new Directory( name, fullpath, uri, parent );
    }

    @Override
    public Directory createDirectory( Directory parentDirectory,
                                      String name ) {
        final Path parentDir = recursiveSearchForDir( getHomeDir(), parentDirectory );
        Path newDir = parentDir.resolve( name );
        if ( !ioService.exists( newDir ) ) {
            createDir( newDir );
        }
        newDir = ioService.get( newDir.toUri() );
        return getDirectory( name, newDir.toString(), newDir.toUri().toString(), parentDirectory );
    }

    @Override
    public Boolean deleteDirectory( String uri ) {
        Path dir = ioService.get( uri );
        return ioService.deleteIfExists( dir, StandardDeleteOption.NON_EMPTY_DIRECTORIES );
    }

    private Path recursiveSearchForDir( Path dir,
                                        Directory parentDirectory ) {
        if ( ioService.exists( dir ) && Files.isDirectory( dir ) ) {
            if ( isThisPathRelativeToThisDir( dir, parentDirectory ) ) {
                return dir;
            } else {
                Path desiredPath = null;
                final DirectoryStream<Path> paths = ioService.newDirectoryStream( dir );
                for ( Path path : paths ) {
                    if ( Files.isDirectory( path ) ) {
                        desiredPath = recursiveSearchForDir( path, parentDirectory );
                    }
                    if ( desiredPath != null ) {
                        break;
                    }
                }
                return desiredPath;
            }

        }
        return null;
    }

    private boolean isThisPathRelativeToThisDir( Path dir,
                                                 Directory parentDirectory ) {
        return dir.getFileName().toString().equals( parentDirectory.getName() );
    }

    private Path getHomeDir() {
        final Path homeDir = root.resolve( HOME_DIR );

        if ( !ioService.exists( homeDir ) ) {
            createDir( homeDir );
        }
        return homeDir;
    }

    private void createDir( Path dir ) {
        final Path dummy_file = dir.resolve( "dummy_file" );
        ioService.write( dummy_file, "." );
    }

}
