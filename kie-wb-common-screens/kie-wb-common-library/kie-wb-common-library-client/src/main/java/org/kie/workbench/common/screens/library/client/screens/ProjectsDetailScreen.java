/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_DETAIL_SCREEN)
public class ProjectsDetailScreen {

    public interface View extends UberElement<ProjectsDetailScreen> {

        void update( String description );

    }

    private View view;

    @Inject
    public ProjectsDetailScreen( final View view ) {
        this.view = view;
    }

    public void update( @Observes final ProjectDetailEvent event ) {
        final POM pom = event.getProjectInfo().getProject().getPom();
        if ( pom != null && pom.getDescription() != null ) {
            view.update( pom.getDescription() );
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Detail Screen";
    }

    @WorkbenchPartView
    public UberElement<ProjectsDetailScreen> getView() {
        return view;
    }
}
