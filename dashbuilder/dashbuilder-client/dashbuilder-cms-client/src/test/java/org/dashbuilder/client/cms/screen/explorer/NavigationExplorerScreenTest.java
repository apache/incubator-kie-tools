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
package org.dashbuilder.client.cms.screen.explorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavTreeLoadedEvent;
import org.dashbuilder.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.client.navigation.widget.editor.NavItemEditorSettings;
import org.dashbuilder.client.navigation.widget.editor.NavTreeEditor;
import org.dashbuilder.navigation.NavTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NavigationExplorerScreenTest {

    @Mock
    NavigationManager navigationManager;

    @Mock
    NavTreeEditor navTreeEditor;

    @Mock
    NavItemEditorSettings navItemEditorSettings;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    ContentManagerI18n i18n;

    @Mock
    NavTree navTree;

    NavigationExplorerScreen navigationExplorerScreen;

    @Before
    public void setUp() {
        when(navTreeEditor.getSettings()).thenReturn(navItemEditorSettings);
        navigationExplorerScreen = new NavigationExplorerScreen(navigationManager, navTreeEditor, i18n, notificationEvent);
    }

    @Test
    public void testInitTreeAlreadySet() {
        when(navigationManager.getNavTree()).thenReturn(navTree);
        navigationExplorerScreen.init();

        verify(navTreeEditor).edit(navTree);
    }

    @Test
    public void testOnNavTreeLoaded() {
        navigationExplorerScreen.init();
        navigationExplorerScreen.onNavTreeLoaded(new NavTreeLoadedEvent(navTree));

        verify(navTreeEditor).edit(navTree);
    }

    @Test
    public void testOnPerspectivesChanged() {
        navigationExplorerScreen.init();
        when(navigationManager.getNavTree()).thenReturn(navTree);
        navigationExplorerScreen.onPerspectivesChanged(new PerspectivePluginsChangedEvent());

        verify(navTreeEditor, times(1)).edit(any(NavTree.class));
    }

    @Test
    public void testOnPerspectivesChangedWithNullNavTree() {
        navigationExplorerScreen.init();
        navigationExplorerScreen.onPerspectivesChanged(new PerspectivePluginsChangedEvent());

        verify(navTreeEditor, times(0)).edit(any(NavTree.class));
    }
}
