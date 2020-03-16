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
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToRawFormatSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNEditorMenuSessionItemsTest {

    @Mock
    private FileMenuBuilder fileMenuBuilder;

    @Mock
    private DMNEditorMenuItemsBuilder builder;

    @Mock
    private DMNEditorSessionCommands sessionCommands;

    @Mock
    private PlaceManager placeManager;

    @Test
    public void testPopulateMenu() {

        final DMNEditorMenuSessionItems menuItems = spy(new DMNEditorMenuSessionItems(builder, sessionCommands, placeManager));
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

        final DMNEditorMenuSessionItems menuItems = spy(new DMNEditorMenuSessionItems(builder, sessionCommands, placeManager));

        menuItems.setEnabled(enabled);
        verify(menuItems).setItemEnabled(ClearSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(VisitGraphSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(SwitchGridSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ValidateSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ExportToJpgSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ExportToPngSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ExportToSvgSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ExportToPdfSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(ExportToRawFormatSessionCommand.class, enabled);
        verify(menuItems).setItemEnabled(DeleteSelectionSessionCommand.class, false);
        verify(menuItems).setItemEnabled(UndoSessionCommand.class, false);
        verify(menuItems).setItemEnabled(RedoSessionCommand.class, false);
        verify(menuItems).setItemEnabled(CopySelectionSessionCommand.class, false);
        verify(menuItems).setItemEnabled(CutSelectionSessionCommand.class, false);
        verify(menuItems).setItemEnabled(PasteSelectionSessionCommand.class, false);
        verify(menuItems).setItemEnabled(DMNPerformAutomaticLayoutCommand.class, enabled);
    }
}