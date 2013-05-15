package org.kie.workbench.common.screens.explorer.backend.server.loaders;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.common.screens.explorer.model.FileItem;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.PackageItem;
import org.kie.workbench.common.screens.explorer.model.ParentPackageItem;
import org.kie.workbench.common.services.backend.file.LinkedDotFileFilter;
import org.kie.workbench.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.kie.workbench.common.services.backend.file.LinkedFilter;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects, Folders and Files
 */
@Dependent
@Named("projectPackageList")
public class ProjectPackageLoader implements ItemsLoader {

    private final LinkedFilter filter;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    public ProjectPackageLoader() {
        filter = new LinkedDotFileFilter();
        filter.setNextFilter( new LinkedMetaInfFolderFilter() );
    }

    @Override
    public List<Item> load( final Path path,
                            final Path projectRoot ) {

        //Check Path exists
        final List<Item> items = new ArrayList<Item>();
        if ( !Files.exists( paths.convert( path ) ) ) {
            return items;
        }

        //Ensure Path represents a Folder
        org.kie.commons.java.nio.file.Path pPath = paths.convert( path );
        if ( !Files.isDirectory( pPath ) ) {
            pPath = pPath.getParent();
        }

        //Get list of immediate children
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( pPath );
        for ( final org.kie.commons.java.nio.file.Path p : directoryStream ) {
            if ( filter.accept( p ) ) {
                if ( Files.isRegularFile( p ) ) {
                    items.add( new FileItem( paths.convert( p ) ) );
                } else if ( Files.isDirectory( p ) ) {
                    items.add( new PackageItem( paths.convert( p ) ) );
                }
            }
        }

        //Add ability to move up one level in the hierarchy
        items.add( new ParentPackageItem( paths.convert( pPath.getParent() ),
                                          ".." ) );

        return items;
    }

}
