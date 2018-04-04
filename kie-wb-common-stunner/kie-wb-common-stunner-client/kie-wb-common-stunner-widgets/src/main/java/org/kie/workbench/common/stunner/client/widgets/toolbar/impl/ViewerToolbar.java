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
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToSvgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;

public class ViewerToolbar extends AbstractToolbar<AbstractClientReadOnlySession> {

    protected final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> items;
    protected final ToolbarCommandFactory commandFactory;

    protected ViewerToolbar(final ToolbarCommandFactory commandFactory,
                  final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> items,
                  final ToolbarView<AbstractToolbar> view) {
        super(view);
        this.commandFactory = commandFactory;
        this.items = items;
        addDefaultCommands();
    }

    @SuppressWarnings("unchecked")
    protected void addDefaultCommands() {
        addCommand(VisitGraphToolbarCommand.class, commandFactory.newVisitGraphCommand());
        addCommand(ClearStatesToolbarCommand.class, commandFactory.newClearStatesCommand());
        addCommand(SwitchGridToolbarCommand.class, commandFactory.newSwitchGridCommand());
        addCommand(ExportToPngToolbarCommand.class, commandFactory.newExportToPngToolbarCommand());
        addCommand(ExportToJpgToolbarCommand.class, commandFactory.newExportToJpgToolbarCommand());
        addCommand(ExportToSvgToolbarCommand.class, commandFactory.newExportToSvgToolbarCommand());
        addCommand(ExportToPdfToolbarCommand.class, commandFactory.newExportToPdfToolbarCommand());
    }

    @Override
    protected AbstractToolbarItem<AbstractClientReadOnlySession> newToolbarItem() {
        return items.get();
    }

    public VisitGraphToolbarCommand getVisitGraphToolbarCommand() {
        return getCommand(VisitGraphToolbarCommand.class);
    }

    public ClearStatesToolbarCommand getClearStatesToolbarCommand() {
        return getCommand(ClearStatesToolbarCommand.class);
    }

    public SwitchGridToolbarCommand getSwitchGridToolbarCommand() {
        return getCommand(SwitchGridToolbarCommand.class);
    }

    public ExportToPngToolbarCommand getExportToPngToolbarCommand() {
        return getCommand(ExportToPngToolbarCommand.class);
    }

    public ExportToJpgToolbarCommand getExportToJpgToolbarCommand() {
        return getCommand(ExportToJpgToolbarCommand.class);
    }

    public ExportToSvgToolbarCommand getExportToSvgToolbarCommand() {
        return getCommand(ExportToSvgToolbarCommand.class);
    }

    public ExportToPdfToolbarCommand getExportToPdfToolbarCommand() {
        return getCommand(ExportToPdfToolbarCommand.class);
    }
}
