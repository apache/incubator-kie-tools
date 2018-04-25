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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

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
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class BPMNDiagramEditorMenuItemsBuilder {

    private final ClientTranslationService translationService;

    @Inject
    public BPMNDiagramEditorMenuItemsBuilder(final ClientTranslationService translationService) {
        this.translationService = translationService;
    }

    public MenuItem newMigrateMenuItem(final Command migrateCommand) {
        final MenuUtils.HasEnabledIsWidget buttonWrapper = MenuUtils.buildHasEnabledWidget(new Button() {{
            setSize(ButtonSize.SMALL);
            setText(translationService.getValue(BPMNClientConstants.EditorMigrateActionMenu));
            addClickHandler(clickEvent -> migrateCommand.execute());
        }});
        return MenuUtils.buildItem(buttonWrapper);
    }

    public MenuItem newFormsGenerationMenuItem(final Command generateProcessForm,
                                               final Command generateAllForms,
                                               final Command generateSelectedForms) {
        final DropDownMenu menu = new DropDownMenu() {{
            setPull(Pull.RIGHT);
        }};

        menu.add(new AnchorListItem(translationService.getValue(BPMNClientConstants.EditorGenerateProcessForm)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(BPMNClientConstants.EditorGenerateProcessForm));
            addClickHandler(event -> generateProcessForm.execute());
        }});
        menu.add(new AnchorListItem(translationService.getValue(BPMNClientConstants.EditorGenerateAllForms)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(BPMNClientConstants.EditorGenerateAllForms));
            addClickHandler(event -> generateAllForms.execute());
        }});
        menu.add(new AnchorListItem(translationService.getValue(BPMNClientConstants.EditorGenerateSelectionForms)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(translationService.getValue(BPMNClientConstants.EditorGenerateSelectionForms));
            addClickHandler(event -> generateSelectedForms.execute());
        }});

        final Button button = new Button() {{
            setToggleCaret(true);
            setDataToggle(Toggle.DROPDOWN);
            setIcon(IconType.LIST_ALT);
            setSize(ButtonSize.SMALL);
            setTitle(translationService.getValue(BPMNClientConstants.EditorFormGenerationTitle));
        }};
        final IsWidget group = MenuUtils.buildHasEnabledWidget(new ButtonGroup() {{
                                                                   add(button);
                                                                   add(menu);
                                                               }},
                                                               button);
        return MenuUtils.buildItem(group);
    }
}
