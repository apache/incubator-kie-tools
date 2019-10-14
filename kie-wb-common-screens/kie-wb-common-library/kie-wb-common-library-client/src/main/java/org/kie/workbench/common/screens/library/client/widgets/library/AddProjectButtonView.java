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

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;

@Templated
public class AddProjectButtonView implements AddProjectButtonPresenter.View,
                                             IsElement {

    private AddProjectButtonPresenter presenter;

    @Inject
    @DataField("add-project")
    Button addProject;

    @Inject
    @DataField("other-projects")
    Button otherProjects;

    @Inject
    @DataField("other-projects-container")
    UnorderedList otherProjectsContainer;

    @Inject
    @DataField("other-projects-header")
    ListItem otherProjectsHeader;

    @Override
    public void init(final AddProjectButtonPresenter presenter) {
        this.presenter = presenter;
        addProject.setHidden(!presenter.userCanCreateProjects());
        otherProjects.setHidden(!presenter.userCanCreateProjects());
    }

    @Override
    public void addOtherProject(final MenuResourceHandlerWidget menuResourceHandlerWidget) {
        otherProjectsContainer.appendChild(menuResourceHandlerWidget.getElement());
    }

    @Override
    public void addNewMenuItem(final MenuResourceHandlerWidget menuResourceHandlerWidget) {
        otherProjectsContainer.insertBefore(menuResourceHandlerWidget.getElement(), otherProjectsHeader);
    }


    @Override
    public void hideOtherProjects() {
        otherProjects.setHidden(true);
    }

    @EventHandler("add-project")
    public void addProject(final ClickEvent event) {
        presenter.addProject();
    }
}
