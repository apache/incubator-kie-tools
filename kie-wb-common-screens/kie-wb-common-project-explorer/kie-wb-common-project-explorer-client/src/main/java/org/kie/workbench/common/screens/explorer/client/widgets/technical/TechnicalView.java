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
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * Technical View definition.
 */
public interface TechnicalView extends HasVisibility {

    void init( final TechnicalViewPresenter presenter );

    void setGroups( final Collection<Group> groups );

    void setRepositories( final Group parentGroup,
                          final Collection<Repository> repositories );

    void setProjects( final Repository parentRepository,
                      final Collection<Project> projects );

}
