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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
@Typed(BPMNProjectEditorMenuSessionItems.class)
public class BPMNProjectEditorMenuSessionItems extends AbstractProcessProjectEditorMenuSessionItems<BPMNProjectDiagramEditorMenuItemsBuilder> {

    private Command onMigrate;
    private MenuItem migrateMenuItem;

    @Inject
    public BPMNProjectEditorMenuSessionItems(final BPMNProjectDiagramEditorMenuItemsBuilder itemsBuilder,
                                             final BPMNEditorSessionCommands sessionCommands) {
        super(itemsBuilder, sessionCommands);
    }

    public BPMNProjectEditorMenuSessionItems setOnMigrate(final Command onMigrate) {
        this.onMigrate = onMigrate;
        return this;
    }

    @Override
    public void populateMenu(final FileMenuBuilder menu) {
        super.populateMenu(menu);
        if (onMigrate != null) {
            migrateMenuItem = newMigrateMenuItem();
            menu.addNewTopLevelMenu(migrateMenuItem);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if (migrateMenuItem != null) {
            migrateMenuItem.setEnabled(enabled);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        onMigrate = null;
        migrateMenuItem = null;
    }

    @Override
    protected String getEditorGenerateProcessFormPropertyKey() {
        return BPMNClientConstants.EditorGenerateProcessForm;
    }

    @Override
    protected String getEditorGenerateAllFormsPropertyKey() {
        return BPMNClientConstants.EditorGenerateAllForms;
    }

    @Override
    protected String getEditorGenerateSelectionFormsPropertyKey() {
        return BPMNClientConstants.EditorGenerateSelectionForms;
    }

    @Override
    protected String getEditorFormGenerationTitlePropertyKey() {
        return BPMNClientConstants.EditorFormGenerationTitle;
    }

    private MenuItem newMigrateMenuItem() {
        final MenuUtils.HasEnabledIsWidget buttonWrapper = MenuUtils.buildHasEnabledWidget(new Button() {{
            setSize(ButtonSize.SMALL);
            setText(getTranslationService().getValue(BPMNClientConstants.EditorMigrateActionMenu));
            addClickHandler(clickEvent -> onMigrate.execute());
        }});
        return MenuUtils.buildItem(buttonWrapper);
    }
}
