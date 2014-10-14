package org.kie.uberfire.apps.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.apps.api.AppsPersistenceAPI;
import org.kie.uberfire.apps.api.Directory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

@Service
@ApplicationScoped
public class AppsPersistenceImpl implements AppsPersistenceAPI {

    public static final String HOME_DIR = "Home";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @PostConstruct
    public void setup() {
    }

    @Override
    public Directory getRootDirectory() {

        Path homeDir = getHomeDir();

        Directory root = new Directory( homeDir.getFileName().toString(), homeDir.toUri().toString() );

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
                    final Directory child = getDirectory( childPath.getFileName().toString(), childPath.toUri().toString(), parent );
                    final List<Directory> childsOfChilds = extractAllChildDirectories( child, childPath );
                    child.addChildDirectories( childsOfChilds );
                    childs.add( child );
                }
            }
        }
        return childs;
    }

    private Directory getDirectory( String name,
                                    String uri,
                                    Directory parent ) {
        return new Directory( name, uri, parent );
    }

    @Override
    public Directory createDirectory( Directory parentDirectory,
                                      String name ) {
        final Path parentDir = recursiveSearchForDir( getHomeDir(), parentDirectory );
        final Path newDir = parentDir.resolve( name );
        if ( !ioService.exists( newDir ) ) {
            createDir( newDir );
        }
        return getDirectory( name, newDir.toUri().toString(), parentDirectory);
    }

    private Path recursiveSearchForDir( Path dir,
                                        Directory parentDirectory ) {
        if ( ioService.exists( dir ) && Files.isDirectory( dir ) ) {
            if ( dir.toUri().toString().equals( parentDirectory.getURI() ) ) {
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

    private Path getHomeDir() {
        Path homeDir = fileSystem.getPath( HOME_DIR );

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
