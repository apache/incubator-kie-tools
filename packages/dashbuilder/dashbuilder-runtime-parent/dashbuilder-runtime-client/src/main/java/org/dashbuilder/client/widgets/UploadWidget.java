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
import elemental2.dom.File;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.screens.Router;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

/**
 * Allow users to upload new dashboards
 *
 */
@Dependent
public class UploadWidget implements IsElement {

    @Inject
    View view;

    @Inject
    Router routerScreen;

    @Inject
    RuntimeClientLoader runtimeClientLoader;

    public interface View extends UberElemental<UploadWidget> {

        void loading();

        void stopLoading();

        void errorLoadingDashboard(String message);

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

    public String getAcceptUpload() {
        return runtimeClientLoader.isClient() ? ".json,.yaml,.yml" : ".zip";
    }

    public void submit(String fileName,
                       final File file,
                       final HTMLFormElement uploadForm) {

        var reader = new FileReader();
        reader.onload = p -> {
            try {

                var loadedContent = runtimeClientLoader.loadContentAndRoute(reader.result.asString());
                if (loadedContent != null) {
                    routerScreen.showRuntimeModel(loadedContent);
                } else {
                    routerScreen.doRoute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                DomGlobal.console.log(e);
                view.errorLoadingDashboard(fileName);
            }
            return null;
        };
        reader.readAsText(file);

    }

    private void openImport(final String newImportName) {
        view.importSuccess(newImportName);
        routerScreen.afterDashboardUpload(newImportName);
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
