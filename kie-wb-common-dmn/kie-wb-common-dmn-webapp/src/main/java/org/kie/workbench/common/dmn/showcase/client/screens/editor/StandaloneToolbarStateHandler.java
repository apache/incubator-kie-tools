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

package org.kie.workbench.common.dmn.showcase.client.screens.editor;

import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;

public class StandaloneToolbarStateHandler implements ToolbarStateHandler {

    private DMNEditorToolbar toolbar;

    //Package-protected for Unit Tests
    private boolean visitGraphToolbarCommandEnabled = false;
    private boolean clearToolbarCommandEnabled = false;
    private boolean deleteSelectionToolbarCommandEnabled = false;
    private boolean switchGridToolbarCommandEnabled = false;
    private boolean undoToolbarCommandEnabled = false;
    private boolean redoToolbarCommandEnabled = false;
    private boolean exportToPngToolbarCommandEnabled = false;
    private boolean exportToJpgToolbarCommandEnabled = false;
    private boolean exportToPdfToolbarCommandEnabled = false;
    private boolean copyCommandEnabled = false;
    private boolean cutCommandEnabled = false;
    private boolean pasteCommandEnabled = false;
    private boolean saveCommandEnabled = false;

    public StandaloneToolbarStateHandler(final DMNEditorToolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void enterGridView() {
        this.visitGraphToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getVisitGraphToolbarCommand());
        this.clearToolbarCommandEnabled = toolbar.isEnabled(toolbar.getClearToolbarCommand());
        this.deleteSelectionToolbarCommandEnabled = toolbar.isEnabled(toolbar.getDeleteSelectionToolbarCommand());
        this.switchGridToolbarCommandEnabled = toolbar.isEnabled(toolbar.getSwitchGridToolbarCommand());
        this.undoToolbarCommandEnabled = toolbar.isEnabled(toolbar.getUndoToolbarCommand());
        this.redoToolbarCommandEnabled = toolbar.isEnabled(toolbar.getRedoToolbarCommand());
        this.exportToPngToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPngToolbarCommand());
        this.exportToJpgToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToJpgToolbarCommand());
        this.exportToPdfToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPdfToolbarCommand());
        this.copyCommandEnabled = toolbar.isEnabled(toolbar.getCopyToolbarCommand());
        this.cutCommandEnabled = toolbar.isEnabled(toolbar.getCutToolbarCommand());
        this.pasteCommandEnabled = toolbar.isEnabled(toolbar.getPasteToolbarCommand());
        this.saveCommandEnabled = toolbar.isEnabled(toolbar.getSaveToolbarCommand());

        enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getClearToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getCopyToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getCutToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getPasteToolbarCommand(),
                             false);
        enableToolbarCommand(toolbar.getSaveToolbarCommand(),
                             false);
    }

    @Override
    public void enterGraphView() {
        enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                             visitGraphToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getClearToolbarCommand(),
                             clearToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                             deleteSelectionToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                             switchGridToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                             undoToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                             redoToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                             exportToPngToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                             exportToJpgToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                             exportToPdfToolbarCommandEnabled);
        enableToolbarCommand(toolbar.getCopyToolbarCommand(),
                             copyCommandEnabled);
        enableToolbarCommand(toolbar.getCutToolbarCommand(),
                             cutCommandEnabled);
        enableToolbarCommand(toolbar.getPasteToolbarCommand(),
                             pasteCommandEnabled);
        enableToolbarCommand(toolbar.getSaveToolbarCommand(),
                             saveCommandEnabled);
    }

    @SuppressWarnings("unchecked")
    private void enableToolbarCommand(final ToolbarCommand command,
                                      final boolean enabled) {
        if (enabled) {
            toolbar.enable(command);
        } else {
            toolbar.disable(command);
        }
    }
}
