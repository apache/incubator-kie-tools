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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.command.SaveDiagramSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNEditorSessionCommandsTest {

    @Mock
    private ManagedClientSessionCommands managedSessionCommands;

    private DMNEditorSessionCommands sessionCommands;

    @Before
    public void setup() {
        this.sessionCommands = new DMNEditorSessionCommands(managedSessionCommands);

        when(managedSessionCommands.register(any())).thenReturn(managedSessionCommands);
    }

    @Test
    public void testRegistration() {
        sessionCommands.registerCommands();

        verify(managedSessionCommands).register(VisitGraphSessionCommand.class);
        verify(managedSessionCommands).register(SwitchGridSessionCommand.class);
        verify(managedSessionCommands).register(ClearSessionCommand.class);
        verify(managedSessionCommands).register(DeleteSelectionSessionCommand.class);
        verify(managedSessionCommands).register(UndoSessionCommand.class);
        verify(managedSessionCommands).register(RedoSessionCommand.class);
        verify(managedSessionCommands).register(ValidateSessionCommand.class);
        verify(managedSessionCommands).register(ExportToPngSessionCommand.class);
        verify(managedSessionCommands).register(ExportToJpgSessionCommand.class);
        verify(managedSessionCommands).register(ExportToPdfSessionCommand.class);
        verify(managedSessionCommands).register(ExportToSvgSessionCommand.class);
        verify(managedSessionCommands).register(ExportToRawFormatSessionCommand.class);
        verify(managedSessionCommands).register(CopySelectionSessionCommand.class);
        verify(managedSessionCommands).register(PasteSelectionSessionCommand.class);
        verify(managedSessionCommands).register(CutSelectionSessionCommand.class);
        verify(managedSessionCommands).register(SaveDiagramSessionCommand.class);
        verify(managedSessionCommands).register(PerformAutomaticLayoutCommand.class);
    }

    @Test
    public void testGetPerformAutomaticLayoutCommand() {
        sessionCommands.getPerformAutomaticLayoutCommand();

        verify(managedSessionCommands).get(PerformAutomaticLayoutCommand.class);
    }
}
