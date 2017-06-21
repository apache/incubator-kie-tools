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

package org.kie.workbench.common.screens.library.client.screens;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetsActionsWidget;
import org.kie.workbench.common.screens.library.client.widgets.project.NewAssetHandlerWidget;
import org.kie.workbench.common.screens.library.client.widgets.project.ProjectActionsWidget;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;

@Templated
public class EmptyProjectView implements EmptyProjectScreen.View,
                                         IsElement {

    private EmptyProjectScreen presenter;

    @Inject
    private ManagedInstance<NewAssetHandlerWidget> resourceHandlerWidgets;

    @Inject
    private ProjectActionsWidget projectActionsWidget;

    @Inject
    private AssetsActionsWidget assetsActionsWidget;

    @Inject
    @DataField("project-toolbar")
    Div projectToolbar;

    @Inject
    @DataField("assets-toolbar")
    Div assetsToolbar;

    @Inject
    @DataField("details-container")
    Div detailsContainer;

    @Inject
    @DataField("resource-handler-container")
    Div resourceHandlerContainer;

    @Inject
    @DataField("browse-more-types")
    Anchor browseMoreTypes;

    @Inject
    @DataField("project-name")
    Div projectNameContainer;

    @Inject
    @DataField("uploader")
    Anchor uploader;

    @Override
    public void init(final EmptyProjectScreen presenter) {
        this.presenter = presenter;
        resourceHandlerContainer.setTextContent("");
        assetsActionsWidget.init();
        projectActionsWidget.init(presenter::goToSettings);
        assetsToolbar.appendChild(assetsActionsWidget.getView().getElement());
        projectToolbar.appendChild(projectActionsWidget.getView().getElement());
    }

    @Override
    public void setProjectName(final String projectName) {
        projectNameContainer.setTextContent(projectName);
    }

    @Override
    public void setProjectDetails(org.jboss.errai.common.client.api.IsElement element) {
        DOMUtil.removeAllChildren(detailsContainer);
        detailsContainer.appendChild(element.getElement());
    }

    @Override
    public void addResourceHandler(final NewResourceHandler newResourceHandler) {
        final NewAssetHandlerWidget newAssetHandlerWidget = resourceHandlerWidgets.get();
        newAssetHandlerWidget.init(newResourceHandler.getDescription(),
                                   newResourceHandler.getIcon(),
                                   newResourceHandler.getCommand(presenter.getNewResourcePresenter()));
        resourceHandlerContainer.appendChild(newAssetHandlerWidget.getElement());
    }

    @EventHandler("browse-more-types")
    public void browseMoreTypes(final ClickEvent clickEvent) {
        resourceHandlerContainer.getClassList().remove("retracted");
        browseMoreTypes.setHidden(true);
    }

    @EventHandler("uploader")
    public void upload(final ClickEvent clickEvent) {
        presenter.getUploadHandler().getCommand(presenter.getNewResourcePresenter()).execute();
    }
}
