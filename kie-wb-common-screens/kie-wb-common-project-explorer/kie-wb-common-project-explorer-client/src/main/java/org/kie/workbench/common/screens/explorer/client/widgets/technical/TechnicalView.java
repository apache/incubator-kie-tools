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
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;

/**
 * Technical View definition.
 */
public interface TechnicalView extends HasBusyIndicator,
                                       HasVisibility {

    void init( final TechnicalViewPresenter presenter );

    void setOrganizationalUnits( final Collection<OrganizationalUnit> organizationalUnits );

    void setRepositories( final Collection<Repository> repositories );

    void setProjects( final Collection<Project> projects );

    void setItems( final FolderListing activeFolderListing );

}
