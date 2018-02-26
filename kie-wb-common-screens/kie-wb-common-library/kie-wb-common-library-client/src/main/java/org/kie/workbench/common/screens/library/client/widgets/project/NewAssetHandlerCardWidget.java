/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.project;

import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class NewAssetHandlerCardWidget {

    private View view;
    private NewResourcePresenter newResourcePresenter;
    private TranslationService ts;
    private Elemental2DomUtil elemental2DomUtil = new Elemental2DomUtil();

    public interface View extends UberElemental<NewAssetHandlerCardWidget> {

        void setDescription(String description);

        void setTitle(String shortName);

        void setIcon(HTMLElement icon);

        void setCommand(Command command);
    }

    @Inject
    public NewAssetHandlerCardWidget(final NewAssetHandlerCardWidget.View view,
                                     final NewResourcePresenter newResourcePresenter,
                                     final TranslationService ts) {
        this.view = view;
        this.newResourcePresenter = newResourcePresenter;
        this.ts = ts;
    }

    public void initialize(NewResourceHandler resourceHandler) {
        ResourceTypeDefinition resourceType = resourceHandler.getResourceType();
        this.view.init(this);
        this.view.setTitle(resourceHandler.getDescription());
        if (resourceType.getCategory() != null) {
            this.view.setDescription(ts.getTranslation(resourceType.getCategory().getName()));
        }
        if (resourceHandler.getIcon() != null) {
            this.view.setIcon(elemental2DomUtil.asHTMLElement(resourceHandler.getIcon().asWidget().getElement()));
        }
        this.view.setCommand(resourceHandler.getCommand(newResourcePresenter));
    }

    public HTMLElement getView() {
        return this.view.getElement();
    }
}
