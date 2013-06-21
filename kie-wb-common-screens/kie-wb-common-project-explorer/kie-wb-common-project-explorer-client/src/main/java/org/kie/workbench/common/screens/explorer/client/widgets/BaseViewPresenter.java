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
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.Collection;

import org.kie.workbench.common.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * The idea is that Project Explorer swaps the "View" it is communicating with depending on whether the Business or Technical views
 * are selected. The Project Explorer's presenter performs the same actions no matter what "View" is selected by calling methods
 * defined on this interface. BusinessView + TechnicalView become redundant. Both implement this interface.
 */
public interface BaseViewPresenter {

    void activate();

    void deactivate();

    void init( final ExplorerPresenter presenter );

    void setGroups( final Collection<Group> groups,
                    final Group selectedGroup );

    void groupSelected( final Group group );

    void setRepositories( final Collection<Repository> repositories,
                          final Repository selectedRepository );

    void repositorySelected( final Repository repository );

    void setProjects( final Collection<Project> projects,
                      final Project selectedProject );

    void projectSelected( final Project project );

    void addRepository( final Repository repository );

    void addProject( final Project project );

}
