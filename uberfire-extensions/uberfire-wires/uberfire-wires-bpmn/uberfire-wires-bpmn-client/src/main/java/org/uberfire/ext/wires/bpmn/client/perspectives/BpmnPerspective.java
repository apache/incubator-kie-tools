/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.ext.wires.bpmn.client.resources.i18n.BpmnEditorConstants;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "BpmnPerspective")
public class BpmnPerspective {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return buildPerspective();
    }

    private PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinitionImpl perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( BpmnEditorConstants.INSTANCE.bpmnPerspectiveTitle() );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "BPMN Explorer" ) ) );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           west );

        return perspective;
    }

}
