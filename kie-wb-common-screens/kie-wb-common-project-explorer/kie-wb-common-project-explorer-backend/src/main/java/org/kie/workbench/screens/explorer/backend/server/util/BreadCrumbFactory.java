package org.kie.workbench.screens.explorer.backend.server.util;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileStore;
import org.kie.workbench.screens.explorer.model.BreadCrumb;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A Factory to make Bread Crumbs!
 */
@ApplicationScoped
public class BreadCrumbFactory {

    private IOService ioService;
    private Paths paths;

    public BreadCrumbFactory() {
        //Required by WELD
    }

    @Inject
    public BreadCrumbFactory( final @Named("ioStrategy") IOService ioService,
                              final Paths paths ) {
        this.ioService = ioService;
        this.paths = paths;
    }

    public List<BreadCrumb> makeBreadCrumbs( final Path path ) {
        return makeBreadCrumbs( path,
                                new ArrayList<org.kie.commons.java.nio.file.Path>(),
                                new HashMap<org.kie.commons.java.nio.file.Path, String>() );
    }

    public List<BreadCrumb> makeBreadCrumbs( final Path path,
                                             final List<org.kie.commons.java.nio.file.Path> exclusions ) {
        return makeBreadCrumbs( path,
                                exclusions,
                                new HashMap<org.kie.commons.java.nio.file.Path, String>() );
    }

    public List<BreadCrumb> makeBreadCrumbs( final Path path,
                                             final List<org.kie.commons.java.nio.file.Path> exclusions,
                                             final Map<org.kie.commons.java.nio.file.Path, String> captionSubstitutions ) {
        final List<BreadCrumb> breadCrumbs = new ArrayList<BreadCrumb>();

        org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        org.kie.commons.java.nio.file.Path nioFileName = nioPath.getFileName();
        while ( nioFileName != null ) {
            if ( includePath( nioPath,
                              exclusions ) ) {
                String caption = nioFileName.toString();
                if ( captionSubstitutions.containsKey( nioPath ) ) {
                    caption = captionSubstitutions.get( nioPath );
                }
                final BreadCrumb breadCrumb = new BreadCrumb( paths.convert( nioPath ),
                                                              caption );
                breadCrumbs.add( 0,
                                 breadCrumb );
            }
            nioPath = nioPath.getParent();
            nioFileName = nioPath.getFileName();
        }
        breadCrumbs.add( 0, new BreadCrumb( paths.convert( nioPath ),
                                            getRootDirectory( nioPath ) ) );

        return breadCrumbs;
    }

    private boolean includePath( final org.kie.commons.java.nio.file.Path path,
                                 final List<org.kie.commons.java.nio.file.Path> exclusions ) {
        for ( final org.kie.commons.java.nio.file.Path p : exclusions ) {
            if ( path.endsWith( p ) ) {
                return false;
            }
        }
        return true;
    }

    private String getRootDirectory( final org.kie.commons.java.nio.file.Path path ) {
        final Iterator<FileStore> fileStoreIterator = path.getFileSystem().getFileStores().iterator();
        if ( fileStoreIterator.hasNext() ) {
            return fileStoreIterator.next().name();
        }
        return "";
    }

}
