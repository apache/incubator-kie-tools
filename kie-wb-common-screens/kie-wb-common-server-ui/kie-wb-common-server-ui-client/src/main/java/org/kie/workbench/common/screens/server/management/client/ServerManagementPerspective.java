/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.server.management.client.events.AddNewContainer;
import org.kie.workbench.common.screens.server.management.client.events.AddNewServerTemplate;
import org.kie.workbench.common.screens.server.management.client.wizard.NewContainerWizard;
import org.kie.workbench.common.screens.server.management.client.wizard.NewServerTemplateWizard;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
@WorkbenchPerspective(identifier = "ServerManagementPerspective")
public class ServerManagementPerspective {

    private final NewServerTemplateWizard newServerTemplateWizard;
    private final NewContainerWizard newContainerWizard;

    @Inject
    public ServerManagementPerspective( final NewServerTemplateWizard newServerTemplateWizard,
                                        final NewContainerWizard newContainerWizard ) {
        this.newServerTemplateWizard = newServerTemplateWizard;
        this.newContainerWizard = newContainerWizard;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "ServerManagementPerspective" );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ServerManagementBrowser" ) ) );

        return perspective;
    }

    public void onNewTemplate( @Observes final AddNewServerTemplate addNewServerTemplate ) {
        checkNotNull( "addNewServerTemplate", addNewServerTemplate );

        newServerTemplateWizard.clear();
        newServerTemplateWizard.start();
    }

    public void onNewContainer( @Observes final AddNewContainer addNewContainer ) {
        checkNotNull( "addNewContainer", addNewContainer );

        newContainerWizard.clear();
        newContainerWizard.setServerTemplate( addNewContainer.getServerTemplate() );
        newContainerWizard.start();
    }

}
