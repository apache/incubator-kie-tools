/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.FormData;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.RouterScreen;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;

/**
 * Allow users to upload new dashboards
 *
 */
@Dependent
public class UploadWidget implements IsElement {

    static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    RouterScreen routerScreen;

    @Inject
    PlaceManager placeManager;

    public interface View extends UberElemental<UploadWidget> {

        void loading();

        void stopLoading();

        void badResponseUploading(Response response);

        void errorDuringUpload(Object error);

        void dashboardAlreadyImportedError(String importName, String modelId);

        void importSuccess(String importName);

    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return this.view.getElement();
    }

    public void submit(String fileName, final HTMLFormElement uploadForm) {
        RequestInit request = RequestInit.create();
        request.setMethod("POST");
        request.setBody(new FormData(uploadForm));
        view.loading();
        DomGlobal.window.fetch("./rest/upload", request)
                        .then((Response response) -> response.text().then(newImportName -> {
                            view.stopLoading();
                            if (response.status == 200) {
                                openImport(newImportName);
                            } 
                            else if(response.status == 409) {
                                importAlreadyExists(fileName, newImportName);
                            } else {
                                view.badResponseUploading(response);
                            }
                            return null;
                        }), error -> {
                            view.stopLoading();
                            view.errorDuringUpload(error);
                            return null;
                        });
    }

    private void openImport(final String newImportName) {
        view.importSuccess(newImportName);
        routerScreen.afterDashboardUpload(newImportName);
    }
    
    private void importAlreadyExists(final String fileName, final String modelId) {
        view.dashboardAlreadyImportedError(fileName, modelId);
        routerScreen.afterDashboardUpload(modelId);
    }

    public String retrieveFileName(String value) {
        int pos = 0;
        if (value.contains("\\")) {
            pos = value.lastIndexOf('\\');
        } else if (value.contains("/")) {
            pos = value.lastIndexOf('/');
        }
        return value.substring(pos + 1);
    }

}