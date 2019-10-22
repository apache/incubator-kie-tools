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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;

public class DMNProjectToolbarStateHandler implements ToolbarStateHandler {

    private static final Class[] COMMAND_CLASSES = {
            ClearSessionCommand.class,
            SwitchGridSessionCommand.class,
            VisitGraphSessionCommand.class,
            DeleteSelectionSessionCommand.class,
            CutSelectionSessionCommand.class,
            CopySelectionSessionCommand.class,
            PasteSelectionSessionCommand.class,
            PerformAutomaticLayoutCommand.class
    };

    private final Map<Class<? extends ClientSessionCommand>, Boolean> commandStates = new HashMap<>();

    private final AbstractDiagramEditorMenuSessionItems projectEditorMenuSessionItems;

    @SuppressWarnings("unchecked")
    public DMNProjectToolbarStateHandler(final AbstractDiagramEditorMenuSessionItems projectEditorMenuSessionItems) {
        this.projectEditorMenuSessionItems = projectEditorMenuSessionItems;

        Arrays.asList(COMMAND_CLASSES).forEach(clazz -> commandStates.put(clazz, false));
    }

    @Override
    public void enterGridView() {
        commandStates.entrySet().forEach(entry -> {
            final Class<? extends ClientSessionCommand> command = entry.getKey();
            entry.setValue(projectEditorMenuSessionItems.isItemEnabled(command));
            projectEditorMenuSessionItems.setItemEnabled(command, false);
        });
    }

    @Override
    public void enterGraphView() {
        commandStates.entrySet().forEach(entry -> {
            final Class<? extends ClientSessionCommand> command = entry.getKey();
            projectEditorMenuSessionItems.setItemEnabled(command, entry.getValue());
        });
    }
}
