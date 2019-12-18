/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.kogito.webapp.base.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = AuthoringPerspective.PERSPECTIVE_ID, isDefault = true)
public class AuthoringPerspective {

    public static final String PERSPECTIVE_ID = "AuthoringPerspective";

    public static final String PERSPECTIVE_NAME = "Authoring";

    @Inject
    protected KogitoScreen kogitoScreen;

    @Inject
    protected PerspectiveConfiguration perspectiveConfiguration;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(perspectiveConfiguration.getPerspectivePanelType().getName());
        perspective.setName(PERSPECTIVE_NAME);
        perspective.getRoot().addPart(new PartDefinitionImpl(kogitoScreen.getPlaceRequest()));
        return perspective;
    }
}
