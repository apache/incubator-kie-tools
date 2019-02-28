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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorMenuItemsBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.Mock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractProcessProjectEditorMenuSessionItemsTest {

    @Mock
    private AbstractProcessEditorSessionCommands sessionCommands;

    @Mock
    private ManagedClientSessionCommands commands;

    @Mock
    private GenerateProcessFormsSessionCommand generateProcessFormsSessionCommand;

    @Mock
    private GenerateDiagramFormsSessionCommand generateDiagramFormsSessionCommand;

    @Mock
    private GenerateSelectedFormsSessionCommand generateSelectedFormsSessionCommand;

    @Mock
    private AbstractProjectDiagramEditorMenuItemsBuilder itemsBuilder;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private FileMenuBuilder menuBuilder;

    @Mock
    private MenuItem menuItem;

    @Mock
    private AnchorListItem anchorListItem;

    @Mock
    private Button button;

    private AbstractProcessProjectEditorMenuSessionItems tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        when(sessionCommands.getGenerateProcessFormsSessionCommand()).thenReturn(generateProcessFormsSessionCommand);
        when(sessionCommands.getGenerateDiagramFormsSessionCommand()).thenReturn(generateDiagramFormsSessionCommand);
        when(sessionCommands.getGenerateSelectedFormsSessionCommand()).thenReturn(generateSelectedFormsSessionCommand);
        when(sessionCommands.getCommands()).thenReturn(commands);

        when(itemsBuilder.getTranslationService()).thenReturn(translationService);
        when(translationService.getValue(anyString())).thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));

        when(menuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(menuBuilder);

        when(itemsBuilder.newClearItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newVisitGraphItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newSwitchGridItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newDeleteSelectionItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newUndoItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newRedoItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newValidateItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newExportsItem(any(Command.class), any(Command.class),
                                         any(Command.class), any(Command.class),
                                         any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newPasteItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newCopyItem(any(Command.class))).thenReturn(menuItem);
        when(itemsBuilder.newCutItem(any(Command.class))).thenReturn(menuItem);

        tested = new AbstractProcessProjectEditorMenuSessionItems(itemsBuilder, sessionCommands) {

            @Override
            protected AnchorListItem createAnchorListItem(String titlePropertyKey, ClickHandler clickHandler) {
                return anchorListItem;
            }

            @Override
            protected Button createButton(String titlePropertyKey) {
                return button;
            }

            @Override
            protected String getEditorGenerateProcessFormPropertyKey() {
                return "EditorGenerateProcessFormPropertyKey";
            }

            @Override
            protected String getEditorGenerateAllFormsPropertyKey() {
                return "EditorGenerateAllFormsPropertyKey";
            }

            @Override
            protected String getEditorGenerateSelectionFormsPropertyKey() {
                return "EditorGenerateSelectionFormsPropertyKey";
            }

            @Override
            protected String getEditorFormGenerationTitlePropertyKey() {
                return "EditorFormGenerationTitlePropertyKey";
            }
        };

        tested.populateMenu(menuBuilder);

        assertNotNull(tested.formsItem);
        tested.formsItem = spy(tested.formsItem);
    }

    @Test
    public void testSetEnabled() throws Exception {
        tested.setEnabled(true);

        verify(tested.formsItem).setEnabled(eq(true));
    }

    @Test
    public void testDestroy() throws Exception {
        tested.destroy();

        assertNull(tested.formsItem);
    }
}