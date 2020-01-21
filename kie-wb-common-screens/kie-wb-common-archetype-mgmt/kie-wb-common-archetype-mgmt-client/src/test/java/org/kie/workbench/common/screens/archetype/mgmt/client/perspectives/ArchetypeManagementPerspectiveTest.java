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

package org.kie.workbench.common.screens.archetype.mgmt.client.perspectives;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.screens.ArchetypeManagementScreenPresenter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypeManagementPerspectiveTest {

    private ArchetypeManagementPerspective perspective;

    @Before
    public void setup() {
        perspective = new ArchetypeManagementPerspective();
    }

    @Test
    public void buildPerspectiveTest() {
        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();

        assertEquals(ArchetypeManagementPerspective.NAME, perspectiveDefinition.getName());
        assertEquals(1, perspectiveDefinition.getRoot().getParts().size());
        assertTrue(perspectiveDefinition.getRoot().getParts().contains(
                new PartDefinitionImpl(new DefaultPlaceRequest(ArchetypeManagementScreenPresenter.IDENTIFIER))));
    }
}