/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasVisibility;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * Technical View definition.
 */
public interface TechnicalView extends HasBusyIndicator,
                                       HasVisibility {

    void init( final TechnicalViewPresenter presenter );

    void setGroups( final Collection<Group> groups );

    void setRepositories( final Collection<Repository> repositories,
                          final Group activeGroup );

    void setProjects( final Collection<Project> projects,
                      final Repository activeRepository,
                      final Group activeGroup );

    void setItems( final FolderListing activeFolderListing,
                   final Project activeProject,
                   final Repository activeRepository,
                   final Group activeGroup );

    void addRepository( final Repository repository );

    void addProject( final Project project );

    void addItem( final FolderItem item );

    void removeItem( final FolderItem item );

}
