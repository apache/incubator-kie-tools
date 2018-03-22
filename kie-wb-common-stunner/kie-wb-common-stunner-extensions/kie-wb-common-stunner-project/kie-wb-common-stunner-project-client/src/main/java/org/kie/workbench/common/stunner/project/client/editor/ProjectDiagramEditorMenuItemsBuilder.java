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
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class ProjectDiagramEditorMenuItemsBuilder {

    private final MenuDevCommandsBuilder menuDevCommandsBuilder;

    private final ClientTranslationService translationService;

    private final PopupUtil popupUtil;

    protected ProjectDiagramEditorMenuItemsBuilder() {
        this(null,
             null,
             null);
    }

    @Inject
    public ProjectDiagramEditorMenuItemsBuilder(final MenuDevCommandsBuilder menuDevCommandsBuilder,
                                                final ClientTranslationService translationService,
                                                final PopupUtil popupUtil) {
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
        this.translationService = translationService;
        this.popupUtil = popupUtil;
    }

    public MenuItem newVisitGraphItem(final Command command) {
        return buildItem(buildVisitGraphItem(command));
    }

    private IsWidget buildVisitGraphItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.PLAY);
                    setTitle(translationService.getValue(CoreTranslationMessages.VISIT_GRAPH));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newSwitchGridItem(final Command command) {
        return buildItem(buildSwitchGridItem(command));
    }

    private IsWidget buildSwitchGridItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.TH);
                    setTitle(translationService.getValue(CoreTranslationMessages.SWITCH_GRID));
                    addClickHandler(clickEvent -> command.execute());
                }});
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
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.ERASER);
                    setTitle(translationService.getValue(CoreTranslationMessages.CLEAR_DIAGRAM));
                    addClickHandler(clickEvent ->
                                            ProjectDiagramEditorMenuItemsBuilder.this.executeWithConfirm(command,
                                                                                                         translationService.getValue(CoreTranslationMessages.CLEAR_DIAGRAM),
                                                                                                         translationService.getValue(CoreTranslationMessages.CLEAR_DIAGRAM),
                                                                                                         translationService.getValue(CoreTranslationMessages.CONFIRM_CLEAR_DIAGRAM)));
                }});
    }

    public MenuItem newCopyItem(final Command command) {
        return buildItem(buildCopyItem(command));
    }

    private IsWidget buildCopyItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.COPY);
                    setTitle(translationService.getValue(CoreTranslationMessages.COPY_SELECTION));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newPasteItem(final Command command) {
        return buildItem(buildPasteItem(command));
    }

    private IsWidget buildPasteItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.PASTE);
                    setTitle(translationService.getValue(CoreTranslationMessages.PASTE_SELECTION));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newCutItem(final Command command) {
        return buildItem(buildCutItem(command));
    }

    private IsWidget buildCutItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.CUT);
                    setTitle(translationService.getValue(CoreTranslationMessages.CUT_SELECTION));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newDeleteSelectionItem(final Command command) {
        return buildItem(buildDeleteSelectionItem(command));
    }

    private IsWidget buildDeleteSelectionItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.TRASH_O);
                    setTitle(translationService.getValue(CoreTranslationMessages.DELETE_SELECTION));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newUndoItem(final Command command) {
        return buildItem(buildUndoItem(command));
    }

    private IsWidget buildUndoItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.UNDO);
                    setTitle(translationService.getValue(CoreTranslationMessages.UNDO));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newRedoItem(final Command command) {
        return buildItem(buildRedoItem(command));
    }

    private IsWidget buildRedoItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.UNDO);
                    //Let's add the icon "manually" since the required icon is not available as IconType value. But the icon
                    //class is available. This assignment is ok for now, since this stuff will sooner or later be refactored to
                    //elemental2.
                    addStyleName("fa-flip-horizontal");
                    setTitle(translationService.getValue(CoreTranslationMessages.REDO));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public MenuItem newExportsItem(final Command exportPNGCommand,
                                   final Command exportJPGCommand,
                                   final Command exportPDFCommand,
                                   final Command exportBPMNCommand) {
        final DropDownMenu menu = new DropDownMenu() {{
            setPull(Pull.RIGHT);
        }};
        menu.add(new AnchorListItem(translationService.getValue(CoreTranslationMessages.EXPORT_PNG)) {{
            setIcon(IconType.FILE_IMAGE_O);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(CoreTranslationMessages.EXPORT_PNG));
            addClickHandler(event -> exportPNGCommand.execute());
        }});
        menu.add(new AnchorListItem(translationService.getValue(CoreTranslationMessages.EXPORT_JPG)) {{
            setIcon(IconType.FILE_IMAGE_O);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(CoreTranslationMessages.EXPORT_JPG));
            addClickHandler(event -> exportJPGCommand.execute());
        }});
        menu.add(new AnchorListItem(translationService.getValue(CoreTranslationMessages.EXPORT_PDF)) {{
            setIcon(IconType.FILE_PDF_O);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(CoreTranslationMessages.EXPORT_PDF));
            addClickHandler(event -> exportPDFCommand.execute());
        }});
        menu.add(new AnchorListItem(translationService.getValue(CoreTranslationMessages.EXPORT_BPMN)) {{
            setIcon(IconType.FILE_TEXT_O);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(CoreTranslationMessages.EXPORT_BPMN));
            addClickHandler(event -> exportBPMNCommand.execute());
        }});

        final IsWidget group = new ButtonGroup() {{
            add(new Button() {{
                setToggleCaret(true);
                setDataToggle(Toggle.DROPDOWN);
                setIcon(IconType.DOWNLOAD);
                setSize(ButtonSize.SMALL);
                setTitle(translationService.getValue(StunnerProjectClientConstants.DOWNLOAD_DIAGRAM));
            }});
            add(menu);
        }};
        return buildItem(group);
    }

    public MenuItem newValidateItem(final Command command) {
        return buildItem(buildValidateItem(command));
    }

    private IsWidget buildValidateItem(final Command command) {
        return MenuUtils.buildHasEnabledWidget(
                new Button() {{
                    setSize(ButtonSize.SMALL);
                    setIcon(IconType.CHECK);
                    setTitle(translationService.getValue(CoreTranslationMessages.VALIDATE));
                    addClickHandler(clickEvent -> command.execute());
                }});
    }

    public boolean isDevItemsEnabled() {
        return menuDevCommandsBuilder.isEnabled();
    }

    public MenuItem newDevItems() {
        return menuDevCommandsBuilder.build();
    }

    private void executeWithConfirm(final Command command,
                                    final String title,
                                    final String okButtonText,
                                    final String confirmMessage) {
        popupUtil.showConfirmPopup(title,
                                   okButtonText,
                                   confirmMessage,
                                   command);
    }

    public static MenuItem buildItem(final IsWidget widget) {
        return MenuUtils.buildItem(widget);
    }
}
