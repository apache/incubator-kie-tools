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

package org.kie.workbench.common.screens.library.client.widgets;

import javax.inject.Inject;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.widgets.client.handlers.NewProjectHandler;
import org.uberfire.mvp.Command;

@Templated
public class NewProjectButtonView implements NewProjectButtonWidget.View,
                                             IsElement {

    private NewProjectButtonWidget presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private ManagedInstance<MenuResourceHandlerWidget> menuProjectHandlerWidgets;

    @Inject
    @DataField("project-handler-menu-container")
    UnorderedList projectHandlerMenuContainer;

    @Inject
    @DataField("import-project-wizard-button")
    Button importWizardButton;

    @Override
    public void init(final NewProjectButtonWidget presenter) {
        this.presenter = presenter;
        projectHandlerMenuContainer.setTextContent("");
    }

    @Override
    public void addNewProjectHandler(final String description,
                                     final NewProjectHandler newProjectHandler) {
        addNewProjectHandler(description,
                             newProjectHandler.getCommand(presenter.getNewResourcePresenter()));
    }

    @Override
    public void addNewProjectHandler(final String description,
                                     final Command command) {
        final MenuResourceHandlerWidget menuProjectHandlerWidget = menuProjectHandlerWidgets.get();
        menuProjectHandlerWidget.init(description,
                                      command);
        projectHandlerMenuContainer.appendChild(menuProjectHandlerWidget.getElement());
    }

    @Override
    public void addHeader(final String title) {
        final ListItem header = (ListItem) DOM.createElement("li");
        header.getClassList().add("dropdown-header");
        header.setTextContent(title);
        projectHandlerMenuContainer.appendChild(header);
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("import-project-wizard-button")
    public void importWizardButton(Event e) {
        presenter.openImportWizard();
    }

    @Override
    public String getDefaultProjectHeaderTitle() {
        return ts.getTranslation(LibraryConstants.DefaultProject);
    }

    @Override
    public String getQuickSetupDescription() {
        return ts.getTranslation(LibraryConstants.QuickSetup);
    }

    @Override
    public String getAdvancedSetupDescription() {
        return ts.getTranslation(LibraryConstants.AdvancedSetup);
    }

    @Override
    public String getOtherProjectsHeaderTitle() {
        return ts.getTranslation(LibraryConstants.OtherProjects);
    }
}
