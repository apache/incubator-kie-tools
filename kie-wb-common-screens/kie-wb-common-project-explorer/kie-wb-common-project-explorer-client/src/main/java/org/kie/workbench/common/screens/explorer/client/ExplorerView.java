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
package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter>,
                              HasBusyIndicator {

    void setGroups( final Collection<Group> groups,
                    final Group activeGroup );

    void setRepositories( final Collection<Repository> repositories,
                          final Repository activeRepository );

    void setProjects( final Collection<Project> projects,
                      final Project activeProject );

    void setPackages( final Collection<Package> packages,
                      final Package activePackage );

    void setItems( final Collection<Item> items );

    void addRepository( final Repository repository );

    void addProject( final Project project );

    void addPackage( final Package pkg );

}
