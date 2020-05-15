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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.cms.screen.transfer.export.wizard.widget.PagesTable;
import org.dashbuilder.common.client.backend.PathUrlFactory;
import org.dashbuilder.common.client.editor.file.FileUploadEditor;
import org.dashbuilder.common.client.editor.file.FileUploadEditor.FileUploadEditorCallback;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.workbench.events.NotificationEvent;

@Templated
public class DataTransferView implements DataTransferScreen.View, IsElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferView.class);
    private DataTransferScreen presenter;
    private ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;
    private HTMLDivElement root;
    private HTMLDivElement fileUploadContainer;
    private HTMLButtonElement btnImport;
    private FileUploadEditor fileUploadEditor;
    private Elemental2DomUtil elem2Dom;
    private Event<NotificationEvent> workbenchNotification;
    private PathUrlFactory pathUrlFactory;
    
    @Inject PagesTable pagesTable;

    public DataTransferView() {
    }

    @Inject
    public DataTransferView(
            final @DataField HTMLDivElement root,
            final @DataField HTMLDivElement fileUploadContainer,
            final @DataField HTMLButtonElement btnImport,
            final @DataField HTMLButtonElement btnExport,
            final @DataField HTMLButtonElement btnGradualExport,
            final FileUploadEditor fileUploadEditor,
            final Elemental2DomUtil elem2Dom,
            final Event<NotificationEvent> workbenchNotification,
            final PathUrlFactory pathUrlFactory) {

        this.root = root;
        this.fileUploadContainer = fileUploadContainer;
        this.btnImport = btnImport;
        this.fileUploadEditor = fileUploadEditor;
        this.elem2Dom = elem2Dom;
        this.workbenchNotification = workbenchNotification;
        this.pathUrlFactory = pathUrlFactory;
    }

    @Override
    public void init(DataTransferScreen presenter) {
        this.presenter = presenter;

        btnImport.disabled = true;

        elem2Dom.appendWidgetToElement(fileUploadContainer, fileUploadEditor.asWidget());

        fileUploadEditor.setAccept(".zip");
        
        fileUploadEditor.configure("fileUpload", new FileUploadEditorCallback() {
            @Override
            public String getUploadFileUrl() {
                String path = new StringBuilder()
                    .append(SpacesAPI.Scheme.DEFAULT)
                    .append("://")
                    .append("system/system")
                    .append("/")
                    .append(presenter.getFilePath())
                    .append("/")
                    .append(presenter.getImportFileName())
                    .toString();

                return pathUrlFactory.getUploadFileUrl(path);
            }

            @Override
            public String getUploadFileName() {
                return presenter.getImportFileName();
            }
        });
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
        btnImport.disabled = true;
        fileUploadEditor.view.clear();
        LOGGER.error(throwable.getMessage(), throwable);
        workbenchNotification.fire(
            new NotificationEvent(
                i18n.importError(),
                NotificationEvent.NotificationType.ERROR));
    }

    @Override
    public void importOK() {
        btnImport.disabled = true;
        fileUploadEditor.view.clear();
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
        presenter.doImport();
    }

    @EventHandler("btnExport")
    public void onExport(ClickEvent event) {
        presenter.doExport();
    }
    
    @EventHandler("btnGradualExport")
    public void onGradualExport(ClickEvent event) {
        presenter.doGradualExport();
    }

    void newFileUploaded(@Observes ValueChangeEvent<String> valueChangeEvent) {
        if (valueChangeEvent.getContext() == fileUploadEditor) {
            btnImport.disabled = false;
        }
    }
}
