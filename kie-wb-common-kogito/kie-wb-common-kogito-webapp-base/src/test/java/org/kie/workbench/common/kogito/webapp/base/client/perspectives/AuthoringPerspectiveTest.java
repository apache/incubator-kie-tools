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

import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AuthoringPerspectiveTest {


    private static final String TESTING_IDENTIFIER = "TESTING_IDENTIFIER";
    private static final PlaceRequest TESTING_REQUEST = new DefaultPlaceRequest(TESTING_IDENTIFIER);
    private static final String RUNNING_IDENTIFIER = "RUNNING_IDENTIFIER";
    private static final PlaceRequest RUNNING_REQUEST = new DefaultPlaceRequest(RUNNING_IDENTIFIER);


    @Test
    public void buildTestingPerspective() {
        final AuthoringPerspective perspective = new AuthoringPerspective();
        perspective.perspectiveConfiguration = new TestingPerspectiveConfiguration();
        perspective.kogitoScreen = getKogitoScreen(TESTING_REQUEST);
        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();
        assertNotNull(perspectiveDefinition);
        assertEquals(MultiListWorkbenchPanelPresenter.class.getName(), perspectiveDefinition.getRoot().getPanelType());
        assertEquals(AuthoringPerspective.PERSPECTIVE_NAME, perspectiveDefinition.getName());
        final Set<PartDefinition> parts = perspectiveDefinition.getRoot().getParts();
        assertEquals(1, parts.size());
        final PartDefinition part = parts.iterator().next();
        assertNotNull(part.getPlace());
        assertTrue(part.getPlace() instanceof DefaultPlaceRequest);
        assertEquals(TESTING_IDENTIFIER, part.getPlace().getIdentifier());
    }

    @Test
    public void buildRuntimePerspective() {
        final AuthoringPerspective perspective = new AuthoringPerspective();
        perspective.perspectiveConfiguration = new PerspectiveConfiguration();
        perspective.kogitoScreen = getKogitoScreen(RUNNING_REQUEST);
        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();
        assertNotNull(perspectiveDefinition);
        assertEquals(StaticWorkbenchPanelPresenter.class.getName(), perspectiveDefinition.getRoot().getPanelType());
        assertEquals(AuthoringPerspective.PERSPECTIVE_NAME, perspectiveDefinition.getName());
        final Set<PartDefinition> parts = perspectiveDefinition.getRoot().getParts();
        assertEquals(1, parts.size());
        final PartDefinition part = parts.iterator().next();
        assertNotNull(part.getPlace());
        assertTrue(part.getPlace() instanceof DefaultPlaceRequest);
        assertEquals(RUNNING_IDENTIFIER, part.getPlace().getIdentifier());
    }



    private KogitoScreen getKogitoScreen(final PlaceRequest placeRequest) {
        return () -> placeRequest;
    }

    private static class TestingPerspectiveConfiguration extends PerspectiveConfiguration {

        @Override
        public Class<? extends AbstractWorkbenchPanelPresenter> getPerspectivePanelType() {
            return MultiListWorkbenchPanelPresenter.class;
        }
    }
}