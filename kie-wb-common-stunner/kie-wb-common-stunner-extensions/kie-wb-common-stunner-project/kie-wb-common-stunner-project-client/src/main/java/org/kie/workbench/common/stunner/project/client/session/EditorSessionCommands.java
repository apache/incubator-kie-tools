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

package org.kie.workbench.common.stunner.project.client.session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
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

@Dependent
public class EditorSessionCommands {

    private final ManagedClientSessionCommands commands;

    @Inject
    public EditorSessionCommands(final ManagedClientSessionCommands commands) {
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        commands.register(VisitGraphSessionCommand.class)
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
                .register(ExportToBpmnSessionCommand.class)
                .register(CopySelectionSessionCommand.class)
                .register(PasteSelectionSessionCommand.class)
                .register(CutSelectionSessionCommand.class);
    }

    public EditorSessionCommands bind(final ClientSession session) {
        commands.bind(session);
        return this;
    }

    public ManagedClientSessionCommands getCommands() {
        return commands;
    }

    public VisitGraphSessionCommand getVisitGraphSessionCommand() {
        return commands.get(0);
    }

    public SwitchGridSessionCommand getSwitchGridSessionCommand() {
        return commands.get(1);
    }

    public ClearSessionCommand getClearSessionCommand() {
        return commands.get(2);
    }

    public DeleteSelectionSessionCommand getDeleteSelectionSessionCommand() {
        return commands.get(3);
    }

    public UndoSessionCommand getUndoSessionCommand() {
        return commands.get(4);
    }

    public RedoSessionCommand getRedoSessionCommand() {
        return commands.get(5);
    }

    public ValidateSessionCommand getValidateSessionCommand() {
        return commands.get(6);
    }

    public ExportToPngSessionCommand getExportToPngSessionCommand() {
        return commands.get(7);
    }

    public ExportToJpgSessionCommand getExportToJpgSessionCommand() {
        return commands.get(8);
    }

    public ExportToPdfSessionCommand getExportToPdfSessionCommand() {
        return commands.get(9);
    }

    public ExportToSvgSessionCommand getExportToSvgSessionCommand() {
        return commands.get(10);
    }

    public ExportToBpmnSessionCommand getExportToBpmnSessionCommand() {
        return commands.get(11);
    }

    public CopySelectionSessionCommand getCopySelectionSessionCommand() {
        return commands.get(12);
    }

    public PasteSelectionSessionCommand getPasteSelectionSessionCommand() {
        return commands.get(13);
    }

    public CutSelectionSessionCommand getCutSelectionSessionCommand() {
        return commands.get(14);
    }

    public <S extends ClientSessionCommand> S get(final int index) {
        return commands.get(index);
    }
}
