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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
@Typed(DMNEditorMenuSessionItems.class)
public class DMNEditorMenuSessionItems extends AbstractDiagramEditorMenuSessionItems<DMNEditorMenuItemsBuilder> {

    @Inject
    public DMNEditorMenuSessionItems(final DMNEditorMenuItemsBuilder itemsBuilder,
                                     final @DMNEditor DMNEditorSessionCommands sessionCommands) {
        super(itemsBuilder,
              sessionCommands);
    }

    @Override
    public void populateMenu(final FileMenuBuilder menu) {
        superPopulateMenu(menu);
        addPerformAutomaticLayout(menu);
    }

    void superPopulateMenu(final FileMenuBuilder menu) {
        super.populateMenu(menu);
    }

    void addPerformAutomaticLayout(final FileMenuBuilder menu) {
        final MenuItem performAutomaticLayoutMenuItem = newPerformAutomaticLayout();
        addMenuItem(PerformAutomaticLayoutCommand.class, performAutomaticLayoutMenuItem);
        menu.addNewTopLevelMenu(performAutomaticLayoutMenuItem);
    }

    MenuItem newPerformAutomaticLayout() {
        final Button button = GWT.create(Button.class);
        button.setSize(ButtonSize.SMALL);
        button.setTitle(getTranslationService().getValue(CoreTranslationMessages.PERFORM_AUTOMATIC_LAYOUT));
        button.setIcon(IconType.SITEMAP);
        button.addClickHandler(clickEvent -> ((DMNEditorSessionCommands) getCommands()).getPerformAutomaticLayoutCommand().execute());

        final MenuUtils.HasEnabledIsWidget buttonWrapper = MenuUtils.buildHasEnabledWidget(button);

        return MenuUtils.buildItem(buttonWrapper);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        superSetEnabled(enabled);
        setItemEnabled(PerformAutomaticLayoutCommand.class, enabled);
    }

    void superSetEnabled(final boolean enabled) {
        super.setEnabled(enabled);
    }
}
