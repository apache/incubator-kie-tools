/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.cms.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.plugin.client.perspective.editor.PerspectiveEditorPresenter;
import org.uberfire.ext.plugin.client.perspective.editor.events.PerspectiveEditorFocusEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ContentManagerPerspectiveTest {

    @Mock
    ContentManagerI18n i18n;

    @Mock
    UberfireDocks uberfireDocks;

    @Mock
    PerspectiveChange perspectiveChangeEvent;

    @Mock
    LayoutEditorPropertiesPresenter propertiesPresenter;

    @Mock
    LayoutEditor layoutEditor;

    @InjectMocks
    ContentManagerPerspective perspective;

    @Before
    public void setUp() {
        when(perspectiveChangeEvent.getIdentifier()).thenReturn("anotherPerspective");
        perspective.init();
        perspective.onOpen();
    }

    @Test
    public void testInit() {
        verify(uberfireDocks).add(perspective.perspectivesExplorerDock);
        verify(uberfireDocks).add(perspective.navigationExplorerDock);
        verify(uberfireDocks).remove(perspective.componentPaletteDock);
        verify(uberfireDocks).open(perspective.perspectivesExplorerDock);
        verify(uberfireDocks).show(UberfireDockPosition.WEST, ContentManagerPerspective.PERSPECTIVE_ID);
    }

    @Test
    public void testOnLayoutEditorFocus() {
        reset(uberfireDocks);
        perspective.onPerspectiveEditorFocus(new PerspectiveEditorFocusEvent(layoutEditor));
        verify(uberfireDocks).add(perspective.componentPaletteDock);
        verify(uberfireDocks).open(perspective.componentPaletteDock);
        verify(uberfireDocks).show(UberfireDockPosition.WEST, ContentManagerPerspective.PERSPECTIVE_ID);
    }

    @Test
    public void testOnLayoutEditorHidden() {
        perspective.onPerspectiveEditorFocus(new PerspectiveEditorFocusEvent(layoutEditor));
        reset(uberfireDocks);
        perspective.onPerspectiveEditorHidden(new PlaceHiddenEvent(new DefaultPlaceRequest(PerspectiveEditorPresenter.ID)));
        verify(uberfireDocks).remove(perspective.componentPaletteDock);
    }
}