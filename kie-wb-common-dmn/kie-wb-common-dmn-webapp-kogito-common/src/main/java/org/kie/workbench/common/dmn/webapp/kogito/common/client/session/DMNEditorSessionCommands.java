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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
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
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;

@Dependent
@DMNEditor
public class DMNEditorSessionCommands extends EditorSessionCommands {

    @Inject
    public DMNEditorSessionCommands(final ManagedClientSessionCommands commands) {
        super(commands);
    }

    @Override
    protected void registerCommands() {
        getCommands().register(VisitGraphSessionCommand.class)
                .register(SwitchGridSessionCommand.class)
                .register(ClearSessionCommand.class)
                .register(DeleteSelectionSessionCommand.class)
                .register(UndoSessionCommand.class)
                .register(RedoSessionCommand.class)
                .register(ValidateSessionCommand.class)
                .register(ExportToPngSessionCommand.class)
                .register(ExportToJpgSessionCommand.class)
                .register(ExportToPdfSessionCommand.class)
                .register(ExportToSvgSessionCommand.class)
                .register(ExportToRawFormatSessionCommand.class)
                .register(CopySelectionSessionCommand.class)
                .register(PasteSelectionSessionCommand.class)
                .register(CutSelectionSessionCommand.class)
                .register(SaveDiagramSessionCommand.class)
                .register(PerformAutomaticLayoutCommand.class);
    }

    public PerformAutomaticLayoutCommand getPerformAutomaticLayoutCommand() {
        return get(PerformAutomaticLayoutCommand.class);
    }
}
