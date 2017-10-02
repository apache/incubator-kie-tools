/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;
import org.kie.workbench.common.screens.library.client.widgets.common.dropdown.DropdownHeaderWidget;
import org.kie.workbench.common.screens.library.client.widgets.common.dropdown.DropdownSeparatorWidget;
import org.uberfire.mvp.Command;

@Templated
public class ImportProjectButtonView implements ImportProjectButtonWidget.View,
                                                IsElement {

    private ImportProjectButtonWidget presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets;

    @Inject
    private ManagedInstance<DropdownHeaderWidget> dropdownHeaderWidgets;

    @Inject
    private ManagedInstance<DropdownSeparatorWidget> dropdownSeparatorWidgets;

    @Inject
    @DataField("import-project-dropdown-container")
    UnorderedList importProjectDropdownContainer;

    @Override
    public void init(final ImportProjectButtonWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearDropdown() {
        importProjectDropdownContainer.setTextContent("");
    }

    @Override
    public void addOption(final String description,
                          final Command command) {
        addOption(description,
                  null,
                  command);
    }

    @Override
    public void addOption(final String description,
                          final String tooltip,
                          final Command command) {
        final MenuResourceHandlerWidget menuResourceHandlerWidget = createMenuResourceHandlerWidget(description,
                                                                                                    tooltip,
                                                                                                    command);
        importProjectDropdownContainer.appendChild(menuResourceHandlerWidget.getElement());
    }

    @Override
    public void addHeader(final String title) {
        final DropdownHeaderWidget header = dropdownHeaderWidgets.get();
        header.init(title);
        importProjectDropdownContainer.appendChild(header.getElement());
    }

    @Override
    public void addSeparator() {
        final DropdownSeparatorWidget separator = dropdownSeparatorWidgets.get();
        importProjectDropdownContainer.appendChild(separator.getElement());
    }

    private MenuResourceHandlerWidget createMenuResourceHandlerWidget(final String description,
                                                                      final String tooltip,
                                                                      final Command command) {
        final MenuResourceHandlerWidget menuResourceHandlerWidget = menuResourceHandlerWidgets.get();
        menuResourceHandlerWidget.init(description,
                                       tooltip,
                                       command);
        return menuResourceHandlerWidget;
    }

    @Override
    public String getImportProjectsHeaderTitle() {
        return ts.getTranslation(LibraryConstants.ImportProjects);
    }

    @Override
    public String getAdvancedImportDescription() {
        return ts.getTranslation(LibraryConstants.AdvancedImport);
    }

    @Override
    public String getImportExamplesHeaderTitle() {
        return ts.getTranslation(LibraryConstants.ImportExamples);
    }
}
