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
package org.kie.workbench.common.screens.contributors.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsConstants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * This perspective display some indicators about the commit activity around the available GIT repositories
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "ContributorsPerspective")
public class ContributorsPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_STATIC );
        p.setTransient(true);
        p.setName( ContributorsConstants.INSTANCE.contributorsPerspectiveName() );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ContributorsScreen" ) ) );
        return p;
    }
}
