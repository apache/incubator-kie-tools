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

package org.kie.workbench.common.dmn.project.client.session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutCommand;
import org.kie.workbench.common.dmn.project.client.session.command.SaveDiagramSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommandsTest;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class DMNEditorSessionCommandsTest extends EditorSessionCommandsTest {

    @Override
    protected EditorSessionCommands makeEditorSessionCommands() {
        return new DMNEditorSessionCommands(commands);
    }

    @Test
    public void testInit() {
        editorSessionCommands.init();

        final InOrder inOrder = inOrder(commands);

        inOrder.verify(commands).register(VisitGraphSessionCommand.class);
        inOrder.verify(commands).register(SwitchGridSessionCommand.class);
        inOrder.verify(commands).register(ClearSessionCommand.class);
        inOrder.verify(commands).register(DeleteSelectionSessionCommand.class);
        inOrder.verify(commands).register(UndoSessionCommand.class);
        inOrder.verify(commands).register(RedoSessionCommand.class);
        inOrder.verify(commands).register(ValidateSessionCommand.class);
        inOrder.verify(commands).register(ExportToPngSessionCommand.class);
        inOrder.verify(commands).register(ExportToJpgSessionCommand.class);
        inOrder.verify(commands).register(ExportToPdfSessionCommand.class);
        inOrder.verify(commands).register(ExportToSvgSessionCommand.class);
        inOrder.verify(commands).register(ExportToBpmnSessionCommand.class);
        inOrder.verify(commands).register(CopySelectionSessionCommand.class);
        inOrder.verify(commands).register(PasteSelectionSessionCommand.class);
        inOrder.verify(commands).register(CutSelectionSessionCommand.class);
        inOrder.verify(commands).register(SaveDiagramSessionCommand.class);
        inOrder.verify(commands).register(DMNPerformAutomaticLayoutCommand.class);
    }
}
