/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.dashbuilder.transfer.DataTransferServices;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;

@ApplicationScoped
@WorkbenchScreen(identifier = DataTransferScreen.ID)
public class DataTransferScreen {

    public static final String ID = "DataTransferScreen";
    private View view;
    private Caller<DataTransferServices> dataTransferServices;
    private ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;
    private DataTransferPopUp popUp;

    public DataTransferScreen() {
    }

    @Inject
    public DataTransferScreen(
            final View view,
            final DataTransferPopUp popUp,
            final Caller<DataTransferServices> dataTransferServices) {

        this.view = view;
        this.popUp = popUp;
        this.dataTransferServices = dataTransferServices;
    }

    @WorkbenchPartTitle
    public String title() {
        return i18n.workbenchPartTitle();
    }

    @WorkbenchPartView
    public View part() {
        return view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void doExport() {
        try {
            dataTransferServices.call(
                (RemoteCallback<String>) path -> {
                    view.exportOK();
                    view.download(path);

                }, (ErrorCallback<Exception>) (message, throwable) -> {
                    view.exportError(throwable);
                    return false;

                }).doExport();

        } catch (Exception e) {
            view.exportError(e);
        }
    }

    public void doImport() {
        try {
            dataTransferServices.call(
                (RemoteCallback<List<String>>) imported -> {
                    view.importOK();
                    popUp.show(imported);

                }, (ErrorCallback<Throwable>) (message, throwable) -> {
                    view.importError(throwable);
                    return false;

                }).doImport();

            } catch (Exception e) {
                view.importError(e);
        }
    }

    public interface View extends UberElemental<DataTransferScreen> {
        void importOK();
        void exportOK();
        void importError(Throwable throwable);
        void exportError(Throwable throwable);
        void download(String path);
    }

    public String getFilePath() {
        return DataTransferServices.FILE_PATH;
    }

    public String getExportFileName() {
        return DataTransferServices.EXPORT_FILE_NAME;
    }

    public String getImportFileName() {
        return DataTransferServices.IMPORT_FILE_NAME;
    }
}
