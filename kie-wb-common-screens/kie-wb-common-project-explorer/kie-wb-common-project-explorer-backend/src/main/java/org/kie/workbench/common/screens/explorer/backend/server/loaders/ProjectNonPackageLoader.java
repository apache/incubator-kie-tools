package org.kie.workbench.common.screens.explorer.backend.server.loaders;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Loader to add Projects, Folders and Files for a Project path that is not within a Package
 */
@Dependent
@Named("projectNonPackageList")
public class ProjectNonPackageLoader implements ItemsLoader {

    @Inject
    @Named("projectRootList")
    private ItemsLoader projectRootListLoader;

    @Override
    public List<Item> load( final Path path,
                            final Path projectRoot ) {
        // A Path that is within a Project but not a Package can be selected from File
        // Explorer. Simply return the Project's content for use in Project Explorer.
        return projectRootListLoader.load( projectRoot,
                                           projectRoot );
    }

}
