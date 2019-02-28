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

package org.kie.workbench.common.stunner.project.client.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
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
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

public abstract class AbstractProjectEditorMenuSessionItems<BUILDER extends AbstractProjectDiagramEditorMenuItemsBuilder> {

    private final BUILDER itemsBuilder;
    private final Map<Class<? extends ClientSessionCommand>, MenuItem> menuItems;
    private final EditorSessionCommands sessionCommands;

    private Command loadingStarts;
    private Command loadingCompleted;
    private Consumer<String> errorConsumer;

    public AbstractProjectEditorMenuSessionItems(final BUILDER itemsBuilder,
                                                 final EditorSessionCommands sessionCommands) {
        this.itemsBuilder = itemsBuilder;
        this.sessionCommands = sessionCommands;
        this.menuItems = new HashMap<>(20);
        this.loadingStarts = () -> {
        };
        this.loadingCompleted = () -> {
        };
        this.errorConsumer = e -> {
        };
    }

    public AbstractProjectEditorMenuSessionItems<BUILDER> setLoadingStarts(final Command loadingStarts) {
        this.loadingStarts = loadingStarts;
        return this;
    }

    public AbstractProjectEditorMenuSessionItems<BUILDER> setLoadingCompleted(final Command loadingCompleted) {
        this.loadingCompleted = loadingCompleted;
        return this;
    }

    public AbstractProjectEditorMenuSessionItems<BUILDER> setErrorConsumer(final Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
        return this;
    }

    public void populateMenu(final FileMenuBuilder menu) {
        // Create the  menu items.
        final MenuItem clearItem = itemsBuilder.newClearItem(this::menu_clear);
        menuItems.put(ClearSessionCommand.class, clearItem);
        final MenuItem visitGraphItem = itemsBuilder.newVisitGraphItem(this::menu_visitGraph);
        menuItems.put(VisitGraphSessionCommand.class, visitGraphItem);
        final MenuItem switchGridItem = itemsBuilder.newSwitchGridItem(this::menu_switchGrid);
        menuItems.put(SwitchGridSessionCommand.class, switchGridItem);
        final MenuItem deleteSelectionItem = itemsBuilder.newDeleteSelectionItem(this::menu_deleteSelected);
        menuItems.put(DeleteSelectionSessionCommand.class, deleteSelectionItem);
        final MenuItem undoItem = itemsBuilder.newUndoItem(this::menu_undo);
        menuItems.put(UndoSessionCommand.class, undoItem);
        final MenuItem redoItem = itemsBuilder.newRedoItem(this::menu_redo);
        menuItems.put(RedoSessionCommand.class, redoItem);
        final MenuItem validateItem = itemsBuilder.newValidateItem(this::validate);
        menuItems.put(ValidateSessionCommand.class, validateItem);
        final MenuItem exportsItem = itemsBuilder.newExportsItem(this::export_imagePNG,
                                                                 this::export_imageJPG,
                                                                 this::export_imageSVG,
                                                                 this::export_imagePDF,
                                                                 this::export_fileBPMN);
        menuItems.put(ExportToPngSessionCommand.class, exportsItem);
        menuItems.put(ExportToJpgSessionCommand.class, exportsItem);
        menuItems.put(ExportToSvgSessionCommand.class, exportsItem);
        menuItems.put(ExportToPdfSessionCommand.class, exportsItem);
        menuItems.put(ExportToBpmnSessionCommand.class, exportsItem);
        final MenuItem pasteItem = itemsBuilder.newPasteItem(this::paste);
        menuItems.put(PasteSelectionSessionCommand.class, pasteItem);
        final MenuItem copyItem = itemsBuilder.newCopyItem(this::menu_copy);
        menuItems.put(CopySelectionSessionCommand.class, copyItem);
        final MenuItem cutItem = itemsBuilder.newCutItem(this::menu_cut);
        menuItems.put(CutSelectionSessionCommand.class, cutItem);

        // Populate the given editor's menu builder.
        menu
                .addNewTopLevelMenu(clearItem)
                .addNewTopLevelMenu(visitGraphItem)
                .addNewTopLevelMenu(switchGridItem)
                .addNewTopLevelMenu(deleteSelectionItem)
                .addNewTopLevelMenu(undoItem)
                .addNewTopLevelMenu(redoItem)
                .addNewTopLevelMenu(validateItem)
                .addNewTopLevelMenu(exportsItem)
                .addNewTopLevelMenu(copyItem)
                .addNewTopLevelMenu(cutItem)
                .addNewTopLevelMenu(pasteItem);
    }

    public void bind(final ClientSession session) {
        // Bind commands to the session and set the right listeners.
        sessionCommands
                .bind(session)
                .getCommands()
                .visit((type, command) -> {
                    command.listen(() -> Optional.ofNullable(menuItems.get(type)).ifPresent(item -> item.setEnabled(command.isEnabled())));
                });
        // Default disabled items.
        setEnabled(session instanceof EditorSession);
    }

    public void setEnabled(final boolean enabled) {
        setItemEnabled(ClearSessionCommand.class, enabled);
        setItemEnabled(VisitGraphSessionCommand.class, enabled);
        setItemEnabled(SwitchGridSessionCommand.class, enabled);
        setItemEnabled(ValidateSessionCommand.class, enabled);
        setItemEnabled(ExportToJpgSessionCommand.class, enabled);
        setItemEnabled(ExportToPngSessionCommand.class, enabled);
        setItemEnabled(ExportToSvgSessionCommand.class, enabled);
        setItemEnabled(ExportToPdfSessionCommand.class, enabled);
        setItemEnabled(ExportToBpmnSessionCommand.class, enabled);

        setItemEnabled(DeleteSelectionSessionCommand.class, false);
        setItemEnabled(UndoSessionCommand.class, false);
        setItemEnabled(RedoSessionCommand.class, false);
        setItemEnabled(CopySelectionSessionCommand.class, false);
        setItemEnabled(CutSelectionSessionCommand.class, false);
        setItemEnabled(PasteSelectionSessionCommand.class, false);
    }

    public EditorSessionCommands getCommands() {
        return sessionCommands;
    }

    public ClientTranslationService getTranslationService() {
        return itemsBuilder.getTranslationService();
    }

    @PreDestroy
    public void destroy() {
        sessionCommands
                .getCommands()
                .visit((type, command) -> {
                    command.listen(null);
                });
        menuItems.clear();
        loadingStarts = null;
        loadingCompleted = null;
        errorConsumer = null;
    }

    protected void addMenuItem(final Class clazz, final MenuItem menuItem){
        this.menuItems.put(clazz, menuItem);
    }

    private void validate() {
        loadingStarts();
        sessionCommands.getValidateSessionCommand().execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
            @Override
            public void onSuccess() {
                loadingCompleted();
            }

            @Override
            public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                AbstractProjectEditorMenuSessionItems.this.onError(violations.toString());
            }
        });
    }

    public void setItemEnabled(final Class<? extends ClientSessionCommand> type,
                               final boolean enabled) {
        if (menuItems.containsKey(type)) {
            menuItems.get(type).setEnabled(enabled);
        }
    }

    public boolean isItemEnabled(final Class<? extends ClientSessionCommand> type) {
        return menuItems.containsKey(type) && menuItems.get(type).isEnabled();
    }

    private void menu_clear() {
        sessionCommands.getClearSessionCommand().execute();
    }

    private void menu_visitGraph() {
        sessionCommands.getVisitGraphSessionCommand().execute();
    }

    private void menu_switchGrid() {
        sessionCommands.getSwitchGridSessionCommand().execute();
    }

    private void menu_deleteSelected() {
        sessionCommands.getDeleteSelectionSessionCommand().execute();
    }

    private void menu_undo() {
        sessionCommands.getUndoSessionCommand().execute();
    }

    private void menu_redo() {
        sessionCommands.getRedoSessionCommand().execute();
    }

    private void export_imagePNG() {
        sessionCommands.getExportToPngSessionCommand().execute();
    }

    private void export_imageJPG() {
        sessionCommands.getExportToJpgSessionCommand().execute();
    }

    private void export_imagePDF() {
        sessionCommands.getExportToPdfSessionCommand().execute();
    }

    private void paste() {
        sessionCommands.getPasteSelectionSessionCommand().execute();
    }

    private void export_imageSVG() {
        sessionCommands.getExportToSvgSessionCommand().execute();
    }

    private void export_fileBPMN() {
        sessionCommands.getExportToBpmnSessionCommand().execute();
    }

    private void menu_copy() {
        sessionCommands.getCopySelectionSessionCommand().execute();
    }

    private void menu_cut() {
        sessionCommands.getCutSelectionSessionCommand().execute();
    }

    protected void loadingStarts() {
        loadingStarts.execute();
    }

    protected void loadingCompleted() {
        loadingCompleted.execute();
    }

    protected void onError(final String error) {
        errorConsumer.accept(error);
    }
}
