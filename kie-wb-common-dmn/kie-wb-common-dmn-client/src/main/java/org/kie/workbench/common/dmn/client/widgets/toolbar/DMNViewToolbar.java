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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;

public class DMNViewToolbar extends ViewerToolbar {

    public DMNViewToolbar(ToolbarCommandFactory commandFactory,
                          ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> items,
                          ToolbarView<AbstractToolbar> view) {
        super(commandFactory, items, view);
    }

    @Override
    protected void addDefaultCommands() {
        addCommand(VisitGraphToolbarCommand.class, commandFactory.newVisitGraphCommand());
        addCommand(ClearStatesToolbarCommand.class, commandFactory.newClearStatesCommand());
        addCommand(SwitchGridToolbarCommand.class, commandFactory.newSwitchGridCommand());
        addCommand(ExportToPngToolbarCommand.class, commandFactory.newExportToPngToolbarCommand());
        addCommand(ExportToJpgToolbarCommand.class, commandFactory.newExportToJpgToolbarCommand());
        addCommand(ExportToPdfToolbarCommand.class, commandFactory.newExportToPdfToolbarCommand());
    }
}
