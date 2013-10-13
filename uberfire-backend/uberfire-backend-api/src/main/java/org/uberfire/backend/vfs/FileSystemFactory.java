package org.uberfire.backend.vfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.backend.vfs.PathFactory.*;

/**
 *
 */
public final class FileSystemFactory {

    private FileSystemFactory() {

    }

    public static FileSystem newFS( final Map<String, String> roots,
                                    final Set<String> supportedViews ) {
        return new FileSystemImpl( roots, supportedViews );
    }

    @Portable
    public static class FileSystemImpl implements FileSystem {

        private List<Path>  rootDirectories = null;
        private Set<String> supportedViews  = null;

        public FileSystemImpl() {
        }

        public FileSystemImpl( final Map<String, String> roots,
                               final Set<String> supportedViews ) {
            checkNotNull( "roots", roots );

            this.rootDirectories = new ArrayList<Path>( roots.size() );

            for ( final Map.Entry<String, String> entry : roots.entrySet() ) {
                this.rootDirectories.add( newPath( this, entry.getValue(), entry.getKey() ) );
            }
            this.supportedViews = new HashSet<String>( checkNotNull( "supportedViews", supportedViews ) );
        }

        @Override
        public List<Path> getRootDirectories() {
            return rootDirectories;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return supportedViews;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if ( rootDirectories != null ) {
                for ( final Path rootDirectory : rootDirectories ) {
                    sb.append( rootDirectory.toString() );
                }
            }
            return sb.toString();
        }
    }

}
