/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;

public class EditorToolbar extends AbstractToolbar<AbstractClientFullSession> {

    private final ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> items;
    private final ToolbarCommandFactory commandFactory;

    EditorToolbar(final ToolbarCommandFactory commandFactory,
                  final ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> items,
                  final ToolbarView<AbstractToolbar> view) {
        super(view);
        this.commandFactory = commandFactory;
        this.items = items;
        addDefaultCommands();
    }

    @SuppressWarnings("unchecked")
    public void addDefaultCommands() {
        addCommand(commandFactory.newVisitGraphCommand());
        addCommand(commandFactory.newClearCommand());
        addCommand(commandFactory.newClearStatesCommand());
        addCommand(commandFactory.newDeleteSelectedElementsCommand());
        addCommand(commandFactory.newSwitchGridCommand());
        addCommand(commandFactory.newUndoCommand());
        addCommand(commandFactory.newRedoCommand());
        addCommand(commandFactory.newValidateCommand());
        addCommand(commandFactory.newExportToPngToolbarCommand());
        addCommand(commandFactory.newExportToJpgToolbarCommand());
        addCommand(commandFactory.newExportToPdfToolbarCommand());
    }

    @Override
    protected AbstractToolbarItem<AbstractClientFullSession> newToolbarItem() {
        return items.get();
    }

    public VisitGraphToolbarCommand getVisitGraphToolbarCommand() {
        return (VisitGraphToolbarCommand) getCommand(0);
    }

    public ClearToolbarCommand getClearToolbarCommand() {
        return (ClearToolbarCommand) getCommand(1);
    }

    public ClearStatesToolbarCommand getClearStatesToolbarCommand() {
        return (ClearStatesToolbarCommand) getCommand(2);
    }

    public DeleteSelectionToolbarCommand getDeleteSelectionToolbarCommand() {
        return (DeleteSelectionToolbarCommand) getCommand(3);
    }

    public SwitchGridToolbarCommand getSwitchGridToolbarCommand() {
        return (SwitchGridToolbarCommand) getCommand(4);
    }

    public UndoToolbarCommand getUndoToolbarCommand() {
        return (UndoToolbarCommand) getCommand(5);
    }

    public RedoToolbarCommand getRedoToolbarCommand() {
        return (RedoToolbarCommand) getCommand(6);
    }

    public ValidateToolbarCommand getValidateToolbarCommand() {
        return (ValidateToolbarCommand) getCommand(7);
    }


    public ExportToPngToolbarCommand getExportToPngToolbarCommand() {
        return (ExportToPngToolbarCommand) getCommand(8);
    }

    public ExportToJpgToolbarCommand getExportToJpgToolbarCommand() {
        return (ExportToJpgToolbarCommand) getCommand(9);
    }

    public ExportToPdfToolbarCommand getExportToPdfToolbarCommand() {
        return (ExportToPdfToolbarCommand) getCommand(10);
    }
}
