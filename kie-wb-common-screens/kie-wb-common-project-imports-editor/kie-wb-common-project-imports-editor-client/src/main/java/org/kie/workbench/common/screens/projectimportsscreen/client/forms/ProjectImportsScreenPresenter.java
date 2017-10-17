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

package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.ProjectImports;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projectimportsscreen.client.resources.i18n.ProjectConfigScreenConstants;
import org.kie.workbench.common.screens.projectimportsscreen.client.type.ProjectImportsResourceType;
import org.kie.workbench.common.services.shared.project.ProjectImportsContent;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = {ProjectImportsResourceType.class})
public class ProjectImportsScreenPresenter
        extends KieEditor {

    private ProjectImportsScreenView view;

    private Caller<ProjectImportsService> importsService;

    private ProjectImports model;

    public ProjectImportsScreenPresenter() {
    }

    @Inject
    public ProjectImportsScreenPresenter(final ProjectImportsScreenView view,
                                         final Caller<ProjectImportsService> importsService) {
        super(view);
        this.view = view;
        this.importsService = importsService;
    }

    @OnStartup
    public void init(final ObservablePath path,
                     final PlaceRequest place) {

        super.init(path,
                   place,
                   new ProjectImportsResourceType());
    }

    private RemoteCallback<ProjectImportsContent> getModelSuccessCallback() {
        return new RemoteCallback<ProjectImportsContent>() {

            @Override
            public void callback(final ProjectImportsContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                model = content.getModel();

                resetEditorPages(content.getOverview());

                view.setContent(model,
                                isReadOnly);
                view.hideBusyIndicator();

                createOriginalHash(content.getModel());
            }
        };
    }

    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                        @Override
                        public void execute() {
                            saveAction();
                        }
                    }))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator)
                    .addDelete(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator);
        }

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu());
    }

    @Override
    protected void loadContent() {
        importsService.call(getModelSuccessCallback(),
                            new HasBusyIndicatorDefaultErrorCallback(view)).loadContent(versionRecordManager.getCurrentPath());
    }

    protected void save() {
        savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                new ParameterizedCommand<String>() {
                                    @Override
                                    public void execute(final String commitMessage) {
                                        view.showSaving();
                                        importsService.call(getSaveSuccessCallback(),
                                                            new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                 model,
                                                                                                                 metadata,
                                                                                                                 commitMessage);
                                    }
                                }
        );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                view.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
                createOriginalHash(model);
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return ProjectConfigScreenConstants.INSTANCE.ExternalImports();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @OnClose
    public void onClose() {
        versionRecordManager.clear();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }
}
