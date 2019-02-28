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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import com.google.gwt.event.dom.client.ClickHandler;
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
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorMenuItemsBuilder;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectEditorMenuSessionItems;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

public abstract class AbstractProcessProjectEditorMenuSessionItems<B extends AbstractProjectDiagramEditorMenuItemsBuilder>
        extends AbstractProjectEditorMenuSessionItems<B> {

    MenuItem formsItem;

    public AbstractProcessProjectEditorMenuSessionItems(B itemsBuilder, EditorSessionCommands sessionCommands) {
        super(itemsBuilder, sessionCommands);
    }

    @Override
    public void populateMenu(final FileMenuBuilder menu) {
        super.populateMenu(menu);

        formsItem = newFormsGenerationMenuItem(() -> executeFormsCommand(getExtendedCommands().getGenerateProcessFormsSessionCommand()),
                                               () -> executeFormsCommand(getExtendedCommands().getGenerateDiagramFormsSessionCommand()),
                                               () -> executeFormsCommand(getExtendedCommands().getGenerateSelectedFormsSessionCommand()));
        menu.addNewTopLevelMenu(formsItem);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        formsItem.setEnabled(enabled);
    }

    @Override
    public void destroy() {
        super.destroy();
        formsItem = null;
    }

    private MenuItem newFormsGenerationMenuItem(final Command generateProcessForm,
                                                final Command generateAllForms,
                                                final Command generateSelectedForms) {
        final DropDownMenu menu = new DropDownMenu() {{
            setPull(Pull.RIGHT);
        }};

        menu.add(createAnchorListItem(getEditorGenerateProcessFormPropertyKey(), event -> generateProcessForm.execute()));
        menu.add(createAnchorListItem(getEditorGenerateAllFormsPropertyKey(), event -> generateAllForms.execute()));
        menu.add(createAnchorListItem(getEditorGenerateSelectionFormsPropertyKey(), event -> generateSelectedForms.execute()));

        final Button button = createButton(getEditorFormGenerationTitlePropertyKey());
        final IsWidget group = MenuUtils.buildHasEnabledWidget(new ButtonGroup() {{
                                                                   add(button);
                                                                   add(menu);
                                                               }},
                                                               button);
        return MenuUtils.buildItem(group);
    }

    protected AnchorListItem createAnchorListItem(final String titlePropertyKey, final ClickHandler clickHandler) {
        return new AnchorListItem(getTranslationService().getValue(titlePropertyKey)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(getTranslationService().getValue(titlePropertyKey));
            addClickHandler(clickHandler);
        }};
    }

    protected Button createButton(final String titlePropertyKey) {
        return new Button() {{
            setToggleCaret(true);
            setDataToggle(Toggle.DROPDOWN);
            setIcon(IconType.LIST_ALT);
            setSize(ButtonSize.SMALL);
            setTitle(getTranslationService().getValue(titlePropertyKey));
        }};
    }

    protected abstract String getEditorGenerateProcessFormPropertyKey();

    protected abstract String getEditorGenerateAllFormsPropertyKey();

    protected abstract String getEditorGenerateSelectionFormsPropertyKey();

    protected abstract String getEditorFormGenerationTitlePropertyKey();

    private void executeFormsCommand(final AbstractClientSessionCommand command) {
        loadingStarts();
        command.execute(new ClientSessionCommand.Callback<ClientRuntimeError>() {
            @Override
            public void onSuccess() {
                loadingCompleted();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                AbstractProcessProjectEditorMenuSessionItems.this.onError(error.getMessage());
            }
        });
    }

    private AbstractProcessEditorSessionCommands getExtendedCommands() {
        return (AbstractProcessEditorSessionCommands) getCommands();
    }
}
