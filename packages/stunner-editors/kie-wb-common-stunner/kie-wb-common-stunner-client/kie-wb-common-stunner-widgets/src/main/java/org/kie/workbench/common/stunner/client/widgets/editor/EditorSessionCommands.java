/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.editor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
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
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SaveDiagramSessionCommand;
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
        registerCommands();
    }

    protected void registerCommands() {
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
                .register(ExportToRawFormatSessionCommand.class)
                .register(CopySelectionSessionCommand.class)
                .register(PasteSelectionSessionCommand.class)
                .register(CutSelectionSessionCommand.class)
                .register(SaveDiagramSessionCommand.class);
    }

    public EditorSessionCommands bind(final ClientSession session) {
        commands.bind(session);
        return this;
    }

    public void clear() {
        commands.clearCommands();
    }

    @PreDestroy
    public void destroy() {
        commands.destroy();
    }

    public ManagedClientSessionCommands getCommands() {
        return commands;
    }

    public VisitGraphSessionCommand getVisitGraphSessionCommand() {
        return commands.get(VisitGraphSessionCommand.class);
    }

    public SwitchGridSessionCommand getSwitchGridSessionCommand() {
        return commands.get(SwitchGridSessionCommand.class);
    }

    public ClearSessionCommand getClearSessionCommand() {
        return commands.get(ClearSessionCommand.class);
    }

    public DeleteSelectionSessionCommand getDeleteSelectionSessionCommand() {
        return commands.get(DeleteSelectionSessionCommand.class);
    }

    public UndoSessionCommand getUndoSessionCommand() {
        return commands.get(UndoSessionCommand.class);
    }

    public RedoSessionCommand getRedoSessionCommand() {
        return commands.get(RedoSessionCommand.class);
    }

    public ValidateSessionCommand getValidateSessionCommand() {
        return commands.get(ValidateSessionCommand.class);
    }

    public ExportToPngSessionCommand getExportToPngSessionCommand() {
        return commands.get(ExportToPngSessionCommand.class);
    }

    public ExportToJpgSessionCommand getExportToJpgSessionCommand() {
        return commands.get(ExportToJpgSessionCommand.class);
    }

    public ExportToPdfSessionCommand getExportToPdfSessionCommand() {
        return commands.get(ExportToPdfSessionCommand.class);
    }

    public ExportToSvgSessionCommand getExportToSvgSessionCommand() {
        return commands.get(ExportToSvgSessionCommand.class);
    }

    public ExportToRawFormatSessionCommand getExportToRawFormatSessionCommand() {
        return commands.get(ExportToRawFormatSessionCommand.class);
    }

    public CopySelectionSessionCommand getCopySelectionSessionCommand() {
        return commands.get(CopySelectionSessionCommand.class);
    }

    public PasteSelectionSessionCommand getPasteSelectionSessionCommand() {
        return commands.get(PasteSelectionSessionCommand.class);
    }

    public CutSelectionSessionCommand getCutSelectionSessionCommand() {
        return commands.get(CutSelectionSessionCommand.class);
    }

    public SaveDiagramSessionCommand getSaveDiagramSessionCommand() {
        return commands.get(SaveDiagramSessionCommand.class);
    }

    public <S extends ClientSessionCommand> S get(final Class<? extends ClientSessionCommand> type) {
        return commands.get(type);
    }
}
