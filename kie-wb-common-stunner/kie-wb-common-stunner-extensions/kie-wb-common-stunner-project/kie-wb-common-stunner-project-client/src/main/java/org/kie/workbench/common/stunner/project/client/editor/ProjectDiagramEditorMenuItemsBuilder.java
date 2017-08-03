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
package org.kie.workbench.common.stunner.project.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconRotate;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

// TODO: I18n.
@ApplicationScoped
public class ProjectDiagramEditorMenuItemsBuilder {

    private final MenuDevCommandsBuilder menuDevCommandsBuilder;

    protected ProjectDiagramEditorMenuItemsBuilder() {
        this(null);
    }

    @Inject
    public ProjectDiagramEditorMenuItemsBuilder(final MenuDevCommandsBuilder menuDevCommandsBuilder) {
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
    }

    public MenuItem newClearSelectionItem(final Command command) {
        return buildItem(buildClearSelectionItem(command));
    }

    private IsWidget buildClearSelectionItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.BAN);
            setTitle("Clear shapes state");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    public MenuItem newVisitGraphItem(final Command command) {
        return buildItem(buildVisitGraphItem(command));
    }

    private IsWidget buildVisitGraphItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.AUTOMOBILE);
            setTitle("Visit graph");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    public MenuItem newSwitchGridItem(final Command command) {
        return buildItem(buildSwitchGridItem(command));
    }

    private IsWidget buildSwitchGridItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.TH);
            setTitle("Switch grid");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    /**
     * Builds a menu item with a clear icon and executes the given callback.
     * Added alert message - the operation cannot be reverted.
     * See <a>org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand</a>
     */
    public MenuItem newClearItem(final Command command) {
        return buildItem(buildClearItem(command));
    }

    private IsWidget buildClearItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.ERASER);
            setTitle("Clear");
            addClickHandler(clickEvent ->
                                    ProjectDiagramEditorMenuItemsBuilder.this.executeWithConfirm(command,
                                                                                                 getConfirmMessage() + " This operation cannot be reverted."));
        }};
    }

    public MenuItem newDeleteSelectionItem(final Command command) {
        return buildItem(buildDeleteSelectionItem(command));
    }

    private IsWidget buildDeleteSelectionItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.TRASH_O);
            setTitle("Delete selected [DEL]");
            addClickHandler(clickEvent -> ProjectDiagramEditorMenuItemsBuilder.this.executeWithConfirm(command));
        }};
    }

    public MenuItem newUndoItem(final Command command) {
        return buildItem(buildUndoItem(command));
    }

    private IsWidget buildUndoItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.UNDO);
            setTitle("Undo [Ctrl+z]");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    public MenuItem newRedoItem(final Command command) {
        return buildItem(buildRedoItem(command));
    }

    private IsWidget buildRedoItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.UNDO);
            setIconRotate(IconRotate.ROTATE_180);
            setTitle("Redo [Ctrl+Shift+z]");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    public MenuItem newExportsItem(final Command exportPNGCommand,
                                   final Command exportJPGCommand,
                                   final Command exportPDFCommand) {
        final DropDownMenu menu = new DropDownMenu() {{
            setPull(Pull.RIGHT);
        }};
        menu.add(new AnchorListItem("Export to PNG") {{
            setIcon(IconType.FILE_IMAGE_O);
            setIconPosition(IconPosition.LEFT);
            setTitle("Export to PNG");
            addClickHandler(event -> exportPNGCommand.execute());
        }});
        menu.add(new AnchorListItem("Export to JPG") {{
            setIcon(IconType.FILE_IMAGE_O);
            setIconPosition(IconPosition.LEFT);
            setTitle("Export to JPG");
            addClickHandler(event -> exportJPGCommand.execute());
        }});
        menu.add(new AnchorListItem("Export to PDF") {{
            setIcon(IconType.FILE_PDF_O);
            setIconPosition(IconPosition.LEFT);
            setTitle("Export to PDF");
            addClickHandler(event -> exportPDFCommand.execute());
        }});
        final IsWidget group = new ButtonGroup() {{
            add(new Button() {{
                setToggleCaret(true);
                setDataToggle(Toggle.DROPDOWN);
                setIcon(IconType.IMAGE);
                setSize(ButtonSize.SMALL);
                setTitle("Export diagram");
            }});
            add(menu);
        }};
        return buildItem(group);
    }

    public MenuItem newValidateItem(final Command command) {
        return buildItem(buildValidateItem(command));
    }

    private IsWidget buildValidateItem(final Command command) {
        return new Button() {{
            setSize(ButtonSize.SMALL);
            setIcon(IconType.CHECK);
            setTitle("Validate");
            addClickHandler(clickEvent -> command.execute());
        }};
    }

    public boolean isDevItemsEnabled() {
        return menuDevCommandsBuilder.isEnabled();
    }

    public MenuItem newDevItems() {
        return menuDevCommandsBuilder.build();
    }

    private IsWidget buildDevItems(final Command switchLogLevelCommand,
                                   final Command logGraphCommand,
                                   final Command logCommandHistoryCommand,
                                   final Command logSessionCommand) {
        final AnchorListItem switchLogLevelItem = new AnchorListItem("Switch log level") {{
            setIcon(IconType.REFRESH);
            addClickHandler(event -> switchLogLevelCommand.execute());
        }};
        final AnchorListItem logSessionItem = new AnchorListItem("Log session") {{
            setIcon(IconType.PRINT);
            addClickHandler(event -> logSessionCommand.execute());
        }};
        final AnchorListItem logGraphItem = new AnchorListItem("Log Graph") {{
            setIcon(IconType.PRINT);
            addClickHandler(event -> logGraphCommand.execute());
        }};
        final AnchorListItem logCommandHistoryItem = new AnchorListItem("Log Command History") {{
            setIcon(IconType.PRINT);
            addClickHandler(event -> logCommandHistoryCommand.execute());
        }};
        return new ButtonGroup() {{
            add(new Button() {{
                setToggleCaret(false);
                setDataToggle(Toggle.DROPDOWN);
                setIcon(IconType.COG);
                setSize(ButtonSize.SMALL);
                setTitle("Development");
            }});
            add(new DropDownMenu() {{
                addStyleName("pull-right");
                add(switchLogLevelItem);
                add(logSessionItem);
                add(logGraphItem);
                add(logCommandHistoryItem);
            }});
        }};
    }

    private void executeWithConfirm(final Command command) {
        this.executeWithConfirm(command,
                                getConfirmMessage());
    }

    private void executeWithConfirm(final Command command,
                                    final String message) {
        final Command yesCommand = command::execute;
        final Command noCommand = () -> {/*Do nothing*/};
        final YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup(getConfirmTitle(),
                                                                            message,
                                                                            yesCommand,
                                                                            noCommand,
                                                                            noCommand);
        popup.show();
    }

    private String getConfirmTitle() {
        return "Confirm action";
    }

    private String getConfirmMessage() {
        return "Are you sure?";
    }

    private MenuItem buildItem(final IsWidget widget) {
        return MenuUtils.buildItem(widget);
    }
}
