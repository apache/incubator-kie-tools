/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * Markdown Live editor
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "HomePerspective", isDefault = true)
public class HomePerspective {

    @Inject
    private Identity identity;

    @Perspective
    public PerspectiveDefinition buildPerspective() {

        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "Home Perspective" );

        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepoList" ) ) );

        return p;
    }

}
