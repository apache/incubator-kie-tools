package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;

import org.kie.workbench.common.services.project.service.model.Package;
import org.kie.workbench.common.services.project.service.model.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter> {

    void setGroups( final Collection<Group> groups );

    void setRepositories( final Collection<Repository> repositories );

    void setProjects( final Collection<Project> projects );

    void setPackages( final Collection<Package> packages );

}
