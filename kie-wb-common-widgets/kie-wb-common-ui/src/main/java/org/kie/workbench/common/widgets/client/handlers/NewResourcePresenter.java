/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

@ApplicationScoped
public class NewResourcePresenter {

    private NewResourceView view;

    private final TranslationService translationService;

    private NewResourceHandler activeHandler = null;

    @Inject
    public NewResourcePresenter(NewResourceView view,
                                TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    private void setup() {
        view.init(this);
    }

    public void show(final NewResourceHandler handler) {
        activeHandler = PortablePreconditions.checkNotNull("handler",
                                                           handler);
        view.show();
        view.setActiveHandler(activeHandler);
        view.setTitle(translationService.getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewPopupTitle) + " " + getActiveHandlerDescription());
    }

    public void validate(final String fileName,
                         final ValidatorWithReasonCallback callback) {
        if (activeHandler != null) {
            activeHandler.validate(fileName,
                                   callback);
        }
    }

    public void makeItem(final String fileName) {
        if (activeHandler != null) {
            activeHandler.create(view.getSelectedPackage(),
                                 fileName,
                                 NewResourcePresenter.this);
        }
    }

    public void complete() {
        view.hide();
    }

    private String getActiveHandlerDescription() {
        if (activeHandler != null) {
            return activeHandler.getDescription();
        } else {
            return "";
        }
    }

    public void setResourceName(String resourceName) {
        view.setResourceName(resourceName);
    }
}
