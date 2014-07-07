package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.HasVisibility;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.Option;

public interface View extends HasBusyIndicator,
                              HasVisibility {

    void init( final ViewPresenter presenter );

    void setContent( final Set<OrganizationalUnit> organizationalUnits,
                     final OrganizationalUnit activeOrganizationalUnit,
                     final Set<Repository> repositories,
                     final Repository activeRepository,
                     final Set<Project> projects,
                     final Project activeProject,
                     final FolderListing folderListing,
                     final Map<FolderItem, List<FolderItem>> siblings );

    void setItems( final FolderListing folderListing );

    void setOptions( final Set<Option> options );

    Explorer getExplorer();
}
