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
package org.kie.workbench.common.dmn.showcase.client.navigator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class DMNDiagramsNavigatorScreenTest {

    private static final String DIAGRAM_NAME = "diagram-name";

    @Mock
    private DiagramsNavigator diagramsNavigator;

    @Mock
    private ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;

    @Mock
    private DMNVFSService vfsService;

    @Mock
    private Path path;

    @Mock
    private KogitoResourceContentService contentService;

    private DMNDiagramsNavigatorScreen navigator;

    @Before
    public void setup() {

        this.navigator = new DMNDiagramsNavigatorScreen(diagramsNavigator,
                                                        newDiagramMenuItemsBuilder,
                                                        vfsService,
                                                        contentService);
    }

    @Test
    public void testEditNoDocumentSelected() {
        navigator.edit();

        verifyNoMoreInteractions(vfsService);
    }

    @Test
    public void testEditDocumentSelected() {
        final LoadDiagramEvent event = new LoadDiagramEvent(path, DIAGRAM_NAME);

        navigator.onLoadDiagramEvent(event);

        navigator.edit();

        verify(vfsService).openFile(eq(path));
    }
}
