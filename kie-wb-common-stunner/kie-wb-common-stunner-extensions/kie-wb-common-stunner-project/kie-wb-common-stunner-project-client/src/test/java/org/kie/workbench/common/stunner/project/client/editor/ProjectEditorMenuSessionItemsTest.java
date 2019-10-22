/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.editor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuItemsBuilder;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectEditorMenuSessionItemsTest {

    @Mock
    private AbstractDiagramEditorMenuItemsBuilder itemsBuilder;

    @Mock
    private EditorSessionCommands sessionCommands;

    @Mock
    private FileMenuBuilder fileMenuBuilder;

    private AbstractDiagramEditorMenuSessionItems editorMenuSessionItems;

    private static class TestAbstractProjectEditorMenuSessionItems extends AbstractDiagramEditorMenuSessionItems<AbstractDiagramEditorMenuItemsBuilder> {

        public TestAbstractProjectEditorMenuSessionItems(final AbstractDiagramEditorMenuItemsBuilder itemsBuilder,
                                                         final EditorSessionCommands sessionCommands) {
            super(itemsBuilder,
                  sessionCommands);
        }
    }

    @Before
    public void setup() {
        editorMenuSessionItems = new TestAbstractProjectEditorMenuSessionItems(itemsBuilder,
                                                                               sessionCommands);
        when(fileMenuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(fileMenuBuilder);
    }

    @Test
    public void testFileMenuBuilder() {
        editorMenuSessionItems.populateMenu(fileMenuBuilder);

        verify(itemsBuilder).newClearItem(any(Command.class));
        verify(itemsBuilder).newVisitGraphItem(any(Command.class));
        verify(itemsBuilder).newSwitchGridItem(any(Command.class));
        verify(itemsBuilder).newDeleteSelectionItem(any(Command.class));
        verify(itemsBuilder).newUndoItem(any(Command.class));
        verify(itemsBuilder).newRedoItem(any(Command.class));
        verify(itemsBuilder).newValidateItem(any(Command.class));
        verify(itemsBuilder).newExportsItem(any(Command.class),
                                            any(Command.class),
                                            any(Command.class),
                                            any(Command.class),
                                            any(Command.class));
        verify(itemsBuilder).newPasteItem(any(Command.class));
        verify(itemsBuilder).newCopyItem(any(Command.class));
        verify(itemsBuilder).newCutItem(any(Command.class));
    }

    @Test
    public void testEnableItemWithRegisteredMenuItem() {
        final MenuItem clearMenuItem = mock(MenuItem.class);
        when(itemsBuilder.newClearItem(any(Command.class))).thenReturn(clearMenuItem);

        editorMenuSessionItems.populateMenu(fileMenuBuilder);

        editorMenuSessionItems.setItemEnabled(ClearSessionCommand.class, true);

        verify(clearMenuItem).setEnabled(true);

        editorMenuSessionItems.setItemEnabled(ClearSessionCommand.class, false);

        verify(clearMenuItem).setEnabled(false);
    }

    @Test
    public void testEnableItemWithUnknownMenuItem() {
        editorMenuSessionItems.populateMenu(fileMenuBuilder);

        try {
            editorMenuSessionItems.setItemEnabled(ClientSessionCommand.class, true);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testIsEnabledWithRegisteredMenuItem() {
        final MenuItem menuItem = new BaseMenuCustom<String>() {

            @Override
            public String build() {
                return "";
            }
        };
        when(itemsBuilder.newClearItem(any(Command.class))).thenReturn(menuItem);

        editorMenuSessionItems.populateMenu(fileMenuBuilder);

        editorMenuSessionItems.setItemEnabled(ClearSessionCommand.class, true);

        assertTrue(menuItem.isEnabled());

        editorMenuSessionItems.setItemEnabled(ClearSessionCommand.class, false);

        assertFalse(menuItem.isEnabled());
    }

    @Test
    public void testIsEnabledWithUnknownMenuItem() {
        final MenuItem menuItem = mock(MenuItem.class);
        when(itemsBuilder.newClearItem(any(Command.class))).thenReturn(menuItem);

        editorMenuSessionItems.populateMenu(fileMenuBuilder);

        try {
            assertFalse(editorMenuSessionItems.isItemEnabled(ClearSessionCommand.class));
        } catch (Exception e) {
            fail(e);
        }
    }
}
