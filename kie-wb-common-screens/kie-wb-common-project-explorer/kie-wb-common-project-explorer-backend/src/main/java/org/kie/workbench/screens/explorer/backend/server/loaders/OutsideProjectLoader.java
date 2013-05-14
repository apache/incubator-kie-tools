package org.kie.workbench.screens.explorer.backend.server.loaders;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.screens.explorer.model.FileItem;
import org.kie.workbench.screens.explorer.model.FolderItem;
import org.kie.workbench.screens.explorer.model.Item;
import org.kie.workbench.screens.explorer.model.ParentFolderItem;
import org.kie.workbench.screens.explorer.model.ProjectItem;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader to add Projects, Folders and Files
 */
@Dependent
@Named("outsideProjectList")
public class OutsideProjectLoader implements ItemsLoader {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private Paths paths;

    @Override
    public List<Item> load( final Path path,
                            final Path projectRoot ) {
        //Project Root is not used by this loader
        return load( path );
    }

    private List<Item> load( final Path path ) {

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
        for ( final org.kie.commons.java.nio.file.Path pChild : directoryStream ) {

            if ( Files.isRegularFile( pChild ) ) {
                items.add( new FileItem( paths.convert( pChild ) ) );

            } else if ( Files.isDirectory( pChild ) ) {

                //Check if Child is a Project Root
                boolean isProject = false;
                final Path childPath = paths.convert( pChild );
                final Path projectRootPath = projectService.resolveProject( childPath );
                if ( projectRootPath != null ) {
                    final org.kie.commons.java.nio.file.Path pRoot = paths.convert( projectRootPath );
                    isProject = Files.isSameFile( pChild,
                                                  pRoot );
                }
                if ( isProject ) {
                    items.add( new ProjectItem( paths.convert( pChild ) ) );
                } else {
                    items.add( new FolderItem( paths.convert( pChild ) ) );
                }
            }
        }

        //Add ability to move up one level in the hierarchy
        items.add( new ParentFolderItem( paths.convert( pPath.getParent() ),
                                         ".." ) );

        return items;
    }

}
