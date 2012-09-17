/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
public class FileExplorerPerspective {

    @Perspective(identifier = "FileExplorerPerspective")
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "File Explorer" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinition();
        west.addPart( new PartDefinition( new PlaceRequest( "FileExplorer" ) ) );
        p.getRoot().setChild( Position.WEST,
                              west );

        return p;
    }

}
