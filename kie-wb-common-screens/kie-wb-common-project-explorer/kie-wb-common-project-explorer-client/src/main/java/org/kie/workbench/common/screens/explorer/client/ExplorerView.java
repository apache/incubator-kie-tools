package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.project.Package;
import org.kie.workbench.common.services.shared.project.Project;
import org.kie.workbench.common.services.shared.project.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter> {

    void setGroups( final Collection<Group> groups,
                    final Group activeGroup );

    void setRepositories( final Collection<Repository> repositories,
                          final Repository activeRepository );

    void setProjects( final Collection<Project> projects,
                      final Project activeProject );

    void setPackages( final Collection<Package> packages,
                      final Package activePackage );

    void setItems( final Collection<Item> items );

}
