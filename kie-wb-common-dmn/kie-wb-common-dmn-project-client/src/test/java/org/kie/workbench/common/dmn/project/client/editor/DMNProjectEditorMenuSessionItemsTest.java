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

package org.kie.workbench.common.dmn.project.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutCommand;
import org.kie.workbench.common.dmn.project.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.Mock;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNProjectEditorMenuSessionItemsTest {

    @Mock
    private FileMenuBuilder fileMenuBuilder;

    @Mock
    private DMNProjectDiagramEditorMenuItemsBuilder builder;

    @Mock
    private DMNEditorSessionCommands sessionCommands;

    @Test
    public void testPopulateMenu() {

        final DMNProjectEditorMenuSessionItems menuItems = spy(new DMNProjectEditorMenuSessionItems(builder, sessionCommands));
        final MenuItem menuItem = mock(MenuItem.class);
        doNothing().when(menuItems).superPopulateMenu(any());
        doReturn(menuItem).when(menuItems).newPerformAutomaticLayout();
        menuItems.populateMenu(fileMenuBuilder);

        verify(menuItems).addPerformAutomaticLayout(any());
    }

    @Test
    public void testEnableMenu() {
        testMenu(true);
    }

    @Test
    public void testDisableMenu() {
        testMenu(false);
    }

    private void testMenu(final boolean enabled) {

        final DMNProjectEditorMenuSessionItems menuItems = spy(new DMNProjectEditorMenuSessionItems(builder, sessionCommands));
        doNothing().when(menuItems).superSetEnabled(enabled);

        menuItems.setEnabled(enabled);

        verify(menuItems).setItemEnabled(DMNPerformAutomaticLayoutCommand.class, enabled);
    }
}