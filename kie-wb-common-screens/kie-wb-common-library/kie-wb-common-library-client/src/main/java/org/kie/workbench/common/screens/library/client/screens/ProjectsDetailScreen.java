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

import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

@WorkbenchScreen( identifier = "ProjectsDetailScreen" )
public class ProjectsDetailScreen {

    public interface View extends UberElement<ProjectsDetailScreen> {

        void update( String description );

    }
    private POM pom;

    private View view;

    private TranslationService ts;

    @Inject
    public ProjectsDetailScreen( View view, TranslationService ts ) {
        this.view = view;
        this.ts = ts;
    }

    public void update( @Observes ProjectDetailEvent event ) {
        pom = event.getProjectSelected().getPom();
        if ( pom != null && pom.getDescription() != null ) {
            view.update( pom.getDescription() );
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation( LibraryConstants.ProjectDetailScreen );
    }

    @WorkbenchPartView
    public UberElement<ProjectsDetailScreen> getView() {
        return view;
    }
}
