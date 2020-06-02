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
package org.dashbuilder.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.FormData;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;

@Dependent
@WorkbenchScreen(identifier = UploadDashboardsScreen.ID)
public class UploadDashboardsScreen {

    public static final String ID = "UploadDashboardsScreen";

    private static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    PlaceManager placeManager;
    
    @Inject
    RuntimeScreen runtimeScreen;

    public interface View extends UberElemental<UploadDashboardsScreen> {

        void loading();

        void stopLoading();

        void badResponseUploading(Response response);

        void errorDuringUpload(Object error);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartTitle
    public String title() {
        return i18n.uploadDashboardsTitle();
    }

    @WorkbenchPartView
    protected View getPart() {
        return view;
    }

    public void submit(HTMLFormElement uploadForm) {
        RequestInit request = RequestInit.create();
        request.setMethod("POST");
        request.setBody(new FormData(uploadForm));
        view.loading();
        DomGlobal.window.fetch("./rest/upload", request)
                        .then((Response response) -> response.text().then(id -> {
                            view.stopLoading();
                            if (response.status == 200) {
                                openModel(id);
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

    protected void openModel(String modelId) {
        runtimeScreen.loadRuntimeModel(modelId);
        placeManager.goTo(RuntimeScreen.ID);
    }

}