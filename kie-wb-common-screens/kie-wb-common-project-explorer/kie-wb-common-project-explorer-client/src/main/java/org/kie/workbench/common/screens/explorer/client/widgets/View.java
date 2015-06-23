/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.HasVisibility;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

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
