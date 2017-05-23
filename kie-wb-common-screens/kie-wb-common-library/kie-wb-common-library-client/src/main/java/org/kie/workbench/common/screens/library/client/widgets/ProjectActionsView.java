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

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Templated
public class ProjectActionsView implements ProjectActionsWidget.View,
                                           IsElement {

    private ProjectActionsWidget presenter;

    private TranslationService translationService;

    @Inject
    @DataField("settings")
    Button settings;

    @Inject
    @DataField("preferences")
    Button preferences;

    @Inject
    @DataField("compile")
    Button compile;

    @Inject
    @DataField("build-and-deploy")
    Button buildAndDeploy;

    @Inject
    @DataField("messages")
    Button messages;

    @Inject
    public ProjectActionsView(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(final ProjectActionsWidget presenter) {
        this.presenter = presenter;

        if (!presenter.userCanBuildProject()) {
            compile.setHidden(true);
            buildAndDeploy.setHidden(true);
        }
    }

    @EventHandler("settings")
    public void projectSettings(final ClickEvent clickEvent) {
        presenter.goToProjectSettings();
    }

    @EventHandler("preferences")
    public void projectPreferences(final ClickEvent clickEvent) {
        presenter.goToPreferences();
    }

    @EventHandler("compile")
    public void compileProject(final ClickEvent clickEvent) {
        presenter.compileProject();
    }

    @EventHandler("build-and-deploy")
    public void buildAndDeployProject(final ClickEvent clickEvent) {
        presenter.buildAndDeployProject();
    }

    @EventHandler("messages")
    public void messages(final ClickEvent clickEvent) {
        presenter.goToMessages();
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void showABuildIsAlreadyRunning() {
        ErrorPopup.showMessage(translationService.getTranslation(LibraryConstants.ABuildIsAlreadyRunning));
    }
}
