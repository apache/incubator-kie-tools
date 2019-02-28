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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CopyToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PasteToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PerformAutomaticLayoutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SaveToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbarDelegate;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

@Default
@DMNEditor
public class DMNEditorToolbar
        extends ManagedToolbarDelegate<EditorSession>
        implements EditorToolbar {

    private final ManagedToolbar<EditorSession> toolbar;

    @Inject
    public DMNEditorToolbar(final ManagedToolbar<EditorSession> toolbar) {
        this.toolbar = toolbar;
    }

    @PostConstruct
    public void init() {
        toolbar.register(VisitGraphToolbarCommand.class)
                .register(ClearToolbarCommand.class)
                .register(DeleteSelectionToolbarCommand.class)
                .register(SwitchGridToolbarCommand.class)
                .register(UndoToolbarCommand.class)
                .register(RedoToolbarCommand.class)
                .register(ValidateToolbarCommand.class)
                .register(ExportToPngToolbarCommand.class)
                .register(ExportToJpgToolbarCommand.class)
                .register(ExportToPdfToolbarCommand.class)
                .register(CopyToolbarCommand.class)
                .register(CutToolbarCommand.class)
                .register(PasteToolbarCommand.class)
                .register(SaveToolbarCommand.class)
                .register(PerformAutomaticLayoutToolbarCommand.class);

    }

    public VisitGraphToolbarCommand getVisitGraphToolbarCommand() {
        return (VisitGraphToolbarCommand) toolbar.getCommand(0);
    }

    public ClearToolbarCommand getClearToolbarCommand() {
        return (ClearToolbarCommand) toolbar.getCommand(1);
    }

    public DeleteSelectionToolbarCommand getDeleteSelectionToolbarCommand() {
        return (DeleteSelectionToolbarCommand) toolbar.getCommand(2);
    }

    public SwitchGridToolbarCommand getSwitchGridToolbarCommand() {
        return (SwitchGridToolbarCommand) toolbar.getCommand(3);
    }

    public UndoToolbarCommand getUndoToolbarCommand() {
        return (UndoToolbarCommand) toolbar.getCommand(4);
    }

    public RedoToolbarCommand getRedoToolbarCommand() {
        return (RedoToolbarCommand) toolbar.getCommand(5);
    }

    public ValidateToolbarCommand getValidateCommand() {
        return (ValidateToolbarCommand) toolbar.getCommand(6);
    }

    public ExportToPngToolbarCommand getExportToPngToolbarCommand() {
        return (ExportToPngToolbarCommand) toolbar.getCommand(7);
    }

    public ExportToJpgToolbarCommand getExportToJpgToolbarCommand() {
        return (ExportToJpgToolbarCommand) toolbar.getCommand(8);
    }

    public ExportToPdfToolbarCommand getExportToPdfToolbarCommand() {
        return (ExportToPdfToolbarCommand) toolbar.getCommand(9);
    }

    public CopyToolbarCommand getCopyToolbarCommand() {
        return (CopyToolbarCommand) toolbar.getCommand(10);
    }

    public CutToolbarCommand getCutToolbarCommand() {
        return (CutToolbarCommand) toolbar.getCommand(11);
    }

    public PasteToolbarCommand getPasteToolbarCommand() {
        return (PasteToolbarCommand) toolbar.getCommand(12);
    }

    public SaveToolbarCommand getSaveToolbarCommand() {
        return (SaveToolbarCommand) toolbar.getCommand(13);
    }

    @Override
    protected ManagedToolbar<EditorSession> getDelegate() {
        return toolbar;
    }
}
