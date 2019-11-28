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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
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
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class DMNEditorMenuSessionItemsTest {

    @Mock
    private FileMenuBuilder fileMenuBuilder;

    @Mock
    private DMNEditorMenuItemsBuilder itemsBuilder;

    @Mock
    private DMNEditorSessionCommands sessionCommands;

    @Mock
    private ClientTranslationService translationService;

    @GwtMock
    @SuppressWarnings("unused")
    private Button button;

    private DMNEditorMenuSessionItems sessionItems;

    @Before
    public void setup() {
        this.sessionItems = spy(new DMNEditorMenuSessionItems(itemsBuilder, sessionCommands));

        when(fileMenuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(fileMenuBuilder);
        when(sessionItems.getTranslationService()).thenReturn(translationService);
        when(translationService.getValue(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testPopulateMenu() {
        final MenuItem menuItem = mock(MenuItem.class);
        doNothing().when(sessionItems).superPopulateMenu(any());
        doReturn(menuItem).when(sessionItems).newPerformAutomaticLayout();
        sessionItems.populateMenu(fileMenuBuilder);

        verify(sessionItems).addPerformAutomaticLayout(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPerformAutomaticLayoutMenuItem() {
        final PerformAutomaticLayoutCommand command = mock(PerformAutomaticLayoutCommand.class);
        final ArgumentCaptor<ClickHandler> clickHandlerArgumentCaptor = ArgumentCaptor.forClass(ClickHandler.class);

        when(sessionCommands.getPerformAutomaticLayoutCommand()).thenReturn(command);

        sessionItems.newPerformAutomaticLayout();

        verify(button).setSize(ButtonSize.SMALL);
        verify(button).setTitle(CoreTranslationMessages.PERFORM_AUTOMATIC_LAYOUT);
        verify(button).setIcon(IconType.SITEMAP);
        verify(button).addClickHandler(clickHandlerArgumentCaptor.capture());

        final ClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();
        clickHandler.onClick(mock(ClickEvent.class));

        verify(command).execute();
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
        sessionItems.setEnabled(enabled);

        verify(sessionItems).setItemEnabled(ClearSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(VisitGraphSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(SwitchGridSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ValidateSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ExportToJpgSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ExportToPngSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ExportToSvgSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ExportToPdfSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(ExportToRawFormatSessionCommand.class, enabled);
        verify(sessionItems).setItemEnabled(DeleteSelectionSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(UndoSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(RedoSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(CopySelectionSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(CutSelectionSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(PasteSelectionSessionCommand.class, false);
        verify(sessionItems).setItemEnabled(PerformAutomaticLayoutCommand.class, enabled);
    }
}
