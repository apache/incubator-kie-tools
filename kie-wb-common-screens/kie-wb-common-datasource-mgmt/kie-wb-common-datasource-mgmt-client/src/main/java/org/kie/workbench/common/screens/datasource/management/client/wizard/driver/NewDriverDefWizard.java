/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.wizard.driver;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewDriverDefWizard
        extends AbstractWizard {

    private final List<WizardPage> pages = new ArrayList<>();

    private DriverDefPage driverDefPage;

    private DriverDef driverDef;

    private Caller<DriverDefEditorService> driverDefService;

    private TranslationService translationService;

    private PopupsUtil popupsUtil;

    private Event<NotificationEvent> notification;

    private Module module;

    @Inject
    public NewDriverDefWizard(final DriverDefPage driverDefPage,
                              final Caller<DriverDefEditorService> driverDefService,
                              final TranslationService translationService,
                              final PopupsUtil popupsUtil,
                              final Event<NotificationEvent> notification) {
        this.driverDefPage = driverDefPage;
        this.driverDefService = driverDefService;
        this.translationService = translationService;
        this.popupsUtil = popupsUtil;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        pages.add(driverDefPage);
    }

    @Override
    public void start() {
        driverDefPage.clear();
        driverDefPage.setComplete(false);
        driverDef = new DriverDef();
        driverDefPage.setDriverDef(driverDef);

        super.start();
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget(int pageNumber) {
        return pages.get(pageNumber).asWidget();
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(DataSourceManagementConstants.NewDriverDefWizard_title);
    }

    @Override
    public int getPreferredHeight() {
        return 600;
    }

    @Override
    public int getPreferredWidth() {
        return 700;
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        driverDefPage.isComplete(callback);
    }

    @Override
    public void complete() {
        doComplete();
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    public void setGlobal() {
        this.module = null;
    }

    private void doComplete() {
        if (isGlobal()) {
            driverDefService.call(getCreateSuccessCallback(),
                                  getCreateErrorCallback()).createGlobal(driverDef);
        } else {
            driverDefService.call(getCreateSuccessCallback(),
                                  getCreateErrorCallback()).create(driverDef,
                                                                   module);
        }
    }

    private RemoteCallback<Path> getCreateSuccessCallback() {
        return new RemoteCallback<Path>() {
            @Override
            public void callback(Path path) {
                notification.fire(new NotificationEvent(
                        translationService.format(DataSourceManagementConstants.NewDriverDefWizard_DriverCreatedMessage,
                                                  path.toString())));
                NewDriverDefWizard.super.complete();
            }
        };
    }

    private ErrorCallback<?> getCreateErrorCallback() {
        return (Message message, Throwable throwable) -> {
                popupsUtil.showErrorPopup(
                        translationService.format(DataSourceManagementConstants.NewDriverDefWizard_DriverCreateErrorMessage,
                                                  buildOnCreateErrorMessage(throwable)));
                return false;
        };
    }

    private String buildOnCreateErrorMessage(Throwable t) {
        if (t instanceof FileAlreadyExistsException) {
            return translationService.format(
                    DataSourceManagementConstants.NewDataSourceDefWizard_FileExistsErrorMessage,
                    ((FileAlreadyExistsException) t).getFile());
        } else {
            return t.getMessage();
        }
    }

    private boolean isGlobal() {
        return module == null;
    }
}
