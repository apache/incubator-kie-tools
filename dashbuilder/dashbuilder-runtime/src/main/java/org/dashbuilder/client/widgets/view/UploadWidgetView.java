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

package org.dashbuilder.client.widgets.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Response;
import org.dashbuilder.client.RuntimeCommunication;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.widgets.UploadWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@Dependent
@Templated
public class UploadWidgetView implements UploadWidget.View {

    private static final AppConstants i18n = AppConstants.INSTANCE;

    UploadWidget presenter;

    @Inject
    @DataField
    HTMLDivElement uploadButtonContainer;

    @Inject
    @DataField
    HTMLButtonElement btnImport;

    @Inject
    @DataField
    HTMLFormElement uploadForm;

    @Inject
    @DataField
    HTMLInputElement inputFile;

    @Inject
    @DataField
    HTMLInputElement inputFileName;

    @Inject
    RuntimeCommunication runtimeCommunication;

    @Inject
    BusyIndicatorView loading;

    @Override
    public void init(UploadWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loading() {
        loading.showBusyIndicator(i18n.uploadingDashboards());
    }

    @Override
    public void stopLoading() {
        loading.hideBusyIndicator();
    }

    @Override
    public void badResponseUploading(Response response) {
        runtimeCommunication.showError(i18n.errorUploadingDashboards(), response);
    }

    @Override
    public void errorDuringUpload(Object error) {
        runtimeCommunication.showError(i18n.errorUploadingDashboards(), error);
    }
    
    @Override
    public void dashboardAlreadyImportedError(String newImportName, String existingImport) {
        runtimeCommunication.showWarning(i18n.dashboardAlreadyImport(newImportName, existingImport));
    }

    @EventHandler("btnImport")
    public void handleImport(ClickEvent e) {
        inputFile.click();
    }

    @EventHandler("inputFile")
    public void handleInputFileChange(ChangeEvent e) {
        String importName = presenter.retrieveFileName(inputFile.value);
        inputFileName.value = importName;
        presenter.submit(importName, uploadForm);
    }

    @Override
    public HTMLElement getElement() {
        return uploadButtonContainer;
    }

    @Override
    public void importSuccess(String importName) {
        runtimeCommunication.showSuccess(i18n.importSuccess(importName));
    }

}