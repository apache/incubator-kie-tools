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

import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

/**
 * Technical View Presenter definition
 */
public interface TechnicalViewPresenter extends BaseViewPresenter {

    void selectGroup( final Group group );

    void selectRepository( final Repository repository );

    void selectProject( final Project project );

    void selectProjectRoot();

    void selectParentFolder( final FolderListing folder );

    void selectFolder( final Path path );

    void selectFile( final Path path );

    Group getActiveGroup();

    Repository getActiveRepository();

    Project getActiveProject();

    FolderListing getActiveFolderListing();

    void reloadActiveProject();
}
