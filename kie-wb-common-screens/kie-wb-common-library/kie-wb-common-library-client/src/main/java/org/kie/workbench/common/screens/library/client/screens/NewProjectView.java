/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.screens;

import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class NewProjectView implements NewProjectScreen.View,
                                       IsElement {

    private NewProjectScreen presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("project-name")
    private Input projectName;

    @Inject
    @DataField("project-description")
    private Input projectDescription;

    @Inject
    @DataField("create")
    private Button create;

    @Inject
    @DataField("cancel")
    private Button cancel;

    @Override
    public void init(final NewProjectScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProjectDescription(final String defaultProjectDescription) {
        this.projectDescription.setValue(defaultProjectDescription);
    }

    @Override
    public String getCreatingProjectMessage() {
        return ts.getTranslation(LibraryConstants.NewProjectScreen_Saving);
    }

    @Override
    public String getProjectCreatedSuccessfullyMessage() {
        return ts.getTranslation(LibraryConstants.ProjectCreated);
    }

    @Override
    public String getEmptyNameMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.ProjectName));
    }

    @Override
    public String getInvalidNameMessage() {
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         ts.getTranslation(LibraryConstants.ProjectName));
    }

    @Override
    public String getDuplicatedProjectMessage() {
        return ts.getTranslation(LibraryConstants.DuplicatedProjectValidation);
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("cancel")
    public void cancel(Event e) {
        presenter.cancel();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("create")
    public void createProject(Event e) {
        presenter.createProject(projectName.getValue(),
                                projectDescription.getValue());
    }
}