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

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.showcase.client.feel.FEELDemoEditor;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
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

    @Mock
    private PlaceManager placeManager;

    @Mock
    private MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> builder;

    @Mock
    private MenuFactory.MenuBuilder<MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder>> menuFactory;

    private DMNDiagramsNavigatorScreen navigator;

    @Before
    public void setup() {
        this.navigator = spy(new DMNDiagramsNavigatorScreen(diagramsNavigator,
                                                            newDiagramMenuItemsBuilder,
                                                            vfsService,
                                                            contentService,
                                                            placeManager));
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

    @Test
    public void testCreateMenuBuilder() {

        final Command onFeelEditorClick = mock(Command.class);
        final Command onLoadFromClientClick = mock(Command.class);

        doReturn(builder).when(navigator).superCreateMenuBuilder();
        doReturn(onFeelEditorClick).when(navigator).onFeelEditorClick();
        doReturn(onLoadFromClientClick).when(navigator).onLoadFromClientClick();

        when(builder.newTopLevelMenu(anyString())).thenReturn(menuFactory);
        when(menuFactory.respondsWith(any(Command.class))).thenReturn(menuFactory);
        when(menuFactory.endMenu()).thenReturn(builder);
        when(menuFactory.order(anyInt())).thenReturn(menuFactory);
        when(menuFactory.endMenu()).thenReturn(builder);

        navigator.createMenuBuilder();

        final InOrder inOrder = inOrder(builder, menuFactory);

        inOrder.verify(builder).newTopLevelMenu("FEEL Editor");
        inOrder.verify(menuFactory).respondsWith(onFeelEditorClick);
        inOrder.verify(builder).newTopLevelMenu("Load diagrams from client");
        inOrder.verify(menuFactory).respondsWith(onLoadFromClientClick);
    }

    @Test
    public void testOnFeelEditorClick() {
        navigator.onFeelEditorClick().execute();
        verify(placeManager).goTo(FEELDemoEditor.EDITOR_ID);
    }

    @Test
    public void testOnLoadFromClientClick() {
        final RemoteCallback<List<String>> items = mock(RemoteCallback.class);
        doReturn(items).when(navigator).getItems();

        navigator.onLoadFromClientClick().execute();

        verify(contentService).getFilteredItems("**/*.dmn", items, null);
    }
}
