/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.common.client.backend.PathUrlFactory;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.workbench.events.NotificationEvent;

@Templated
public class DataTransferView implements DataTransferScreen.View, IsElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferView.class);
    private DataTransferScreen presenter;
    private ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;
    private HTMLDivElement root;
    private HTMLButtonElement btnImport;
    private Event<NotificationEvent> workbenchNotification;
    private PathUrlFactory pathUrlFactory;
    private HTMLInputElement inputFile;
    private HTMLFormElement uploadForm;
    
    public DataTransferView() {
    }

    @Inject
    public DataTransferView(
            final @DataField HTMLDivElement root,
            final @DataField HTMLButtonElement btnImport,
            final @DataField HTMLButtonElement btnExport,
            final @DataField HTMLButtonElement btnGradualExport,
            final @DataField HTMLInputElement inputFile,
            final @DataField HTMLFormElement uploadForm,
            final Event<NotificationEvent> workbenchNotification,
            final PathUrlFactory pathUrlFactory) {

        this.root = root;
        this.btnImport = btnImport;
        this.inputFile = inputFile;
        this.uploadForm = uploadForm;
        this.workbenchNotification = workbenchNotification;
        this.pathUrlFactory = pathUrlFactory;
    }

    @Override
    public void init(DataTransferScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }

    @Override
    public void download(String path) {
        DomGlobal.window.open(
            pathUrlFactory.getDownloadFileUrl(path));
    }

    @Override
    public void exportError(Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
        workbenchNotification.fire(
            new NotificationEvent(
                i18n.exportError(),
                NotificationEvent.NotificationType.ERROR));
    }

    @Override
    public void importError(Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
        workbenchNotification.fire(
            new NotificationEvent(
                i18n.importError(),
                NotificationEvent.NotificationType.ERROR));
    }

    @Override
    public void importOK() {
        workbenchNotification.fire(
            new NotificationEvent(
                i18n.importOK(),
                NotificationEvent.NotificationType.SUCCESS));
    }

    @Override
    public void exportOK() {
        workbenchNotification.fire(
            new NotificationEvent(
                i18n.exportOK(),
                NotificationEvent.NotificationType.SUCCESS));
    }

    @EventHandler("btnImport")
    public void onImport(ClickEvent event) {
        inputFile.click();
    }

    @EventHandler("btnExport")
    public void onExport(ClickEvent event) {
        presenter.doExport();
    }
    
    @EventHandler("btnGradualExport")
    public void onGradualExport(ClickEvent event) {
        presenter.doGradualExport();
    }
    
    @EventHandler("inputFile")
    public void handleInputFileChange(ChangeEvent e) {
        presenter.doImport(uploadForm);
    }
    
    public void clearSelection() {
        uploadForm.reset();
    }

    @Override
    public void openUrl(String url) {
        DomGlobal.window.open(url, "_blank");
    }
    
}
