/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.project.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNEditorMenuSessionItemsValidateTest {

    @Mock
    private DMNEditorMenuItemsBuilder builder;

    @Mock
    private DMNEditorSessionCommands sessionCommands;

    @Mock
    private ValidateSessionCommand validateSessionCommand;

    @Mock
    private PlaceManager placeManager;

    @Captor
    private ArgumentCaptor<Command> validateItemArgumentCaptor;

    @Captor
    private ArgumentCaptor<ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>> violationsArgumentCapture;

    private DMNEditorMenuSessionItems menuItems;

    @Before
    public void setUp() {
        menuItems = new DMNEditorMenuSessionItems(builder,
                                                  sessionCommands,
                                                  placeManager) {
            @Override
            MenuItem newPerformAutomaticLayout() {
                return mock(MenuItem.class);
            }
        };

        final FileMenuBuilder fileMenuBuilder = mock(FileMenuBuilder.class);
        doReturn(fileMenuBuilder).when(fileMenuBuilder).addNewTopLevelMenu(any());
        menuItems.populateMenu(fileMenuBuilder);
        verify(builder).newValidateItem(validateItemArgumentCaptor.capture());

        doReturn(validateSessionCommand).when(sessionCommands).getValidateSessionCommand();

        validateItemArgumentCaptor.getValue().execute();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());
    }

    @Test
    public void openAlertsPanelAfterSuccess() {
        verify(placeManager, never()).goTo(MessageConsoleScreen.ALERTS);

        violationsArgumentCapture.getValue().onSuccess();

        verify(placeManager).goTo(MessageConsoleScreen.ALERTS);
    }

    @Test
    public void openAlertsPanelAfterFailure() {
        verify(placeManager, never()).goTo(MessageConsoleScreen.ALERTS);

        violationsArgumentCapture.getValue().onError(new HashSet<>());

        verify(placeManager).goTo(MessageConsoleScreen.ALERTS);
    }
}