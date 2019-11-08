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
package org.kie.workbench.common.dmn.showcase.client.perspectives;

import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.BaseDMNDiagramEditor;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthoringPerspectiveTest {

    @Test
    public void testStructure() {
        final AuthoringPerspective perspective = new AuthoringPerspective();

        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();

        assertThat(perspectiveDefinition).isNotNull();
        assertThat(perspectiveDefinition.getRoot().getPanelType()).isEqualTo(StaticWorkbenchPanelPresenter.class.getName());
        assertThat(perspectiveDefinition.getName()).isEqualTo(AuthoringPerspective.PERSPECTIVE_NAME);

        final Set<PartDefinition> parts = perspectiveDefinition.getRoot().getParts();
        assertThat(parts).hasSize(1);
        final PartDefinition part = parts.iterator().next();
        assertThat(part.getPlace()).isInstanceOf(DefaultPlaceRequest.class);
        assertThat(part.getPlace().getIdentifier()).isEqualTo(BaseDMNDiagramEditor.EDITOR_ID);
    }
}
