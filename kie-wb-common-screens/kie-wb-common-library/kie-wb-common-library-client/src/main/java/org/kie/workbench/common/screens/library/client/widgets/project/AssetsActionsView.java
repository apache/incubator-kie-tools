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

package org.kie.workbench.common.screens.library.client.widgets.project;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;

@Templated
public class AssetsActionsView implements AssetsActionsWidget.View,
                                          IsElement {

    private AssetsActionsWidget presenter;

    @Inject
    private ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets;

    @Inject
    @DataField("resource-handler-menu-container")
    UnorderedList resourceHandlerMenuContainer;

    @Override
    public void init(final AssetsActionsWidget presenter) {
        this.presenter = presenter;
        resourceHandlerMenuContainer.setTextContent("");
    }

    @Override
    public void addResourceHandler(final NewResourceHandler newResourceHandler) {
        final MenuResourceHandlerWidget menuResourceHandlerWidget = menuResourceHandlerWidgets.get();
        menuResourceHandlerWidget.init(newResourceHandler.getDescription(),
                                       newResourceHandler.getCommand(presenter.getNewResourcePresenter()));
        resourceHandlerMenuContainer.appendChild(menuResourceHandlerWidget.getElement());
    }
}
