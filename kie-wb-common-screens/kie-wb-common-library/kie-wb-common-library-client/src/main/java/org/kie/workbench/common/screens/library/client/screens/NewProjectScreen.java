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

package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.NEW_PROJECT_SCREEN)
public class NewProjectScreen {

    public interface View extends UberElement<NewProjectScreen> {

    }

    LibraryInfo libraryInfo;
    private Caller<LibraryService> libraryService;

    private PlaceManager placeManager;

    private BusyIndicatorView busyIndicatorView;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPlaces libraryPlaces;

    private View view;

    private TranslationService ts;

    private SessionInfo sessionInfo;

    private Event<NewProjectEvent> newProjectEvent;

    @Inject
    public NewProjectScreen(final Caller<LibraryService> libraryService,
                            final PlaceManager placeManager,
                            final BusyIndicatorView busyIndicatorView,
                            final Event<NotificationEvent> notificationEvent,
                            final LibraryPlaces libraryPlaces,
                            final View view,
                            final TranslationService ts,
                            final SessionInfo sessionInfo,
                            final Event<NewProjectEvent> newProjectEvent) {
        this.libraryService = libraryService;
        this.placeManager = placeManager;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.view = view;
        this.ts = ts;
        this.sessionInfo = sessionInfo;
        this.newProjectEvent = newProjectEvent;
    }

    @OnStartup
    public void load() {
        view.init(this);
        libraryService.call(new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback(LibraryInfo libraryInfo) {
                NewProjectScreen.this.libraryInfo = libraryInfo;
            }
        }).getLibraryInfo(libraryPlaces.getSelectedRepository(),
                          libraryPlaces.getSelectedBranch());
    }

    public void cancel() {
        libraryPlaces.goToLibrary();
        placeManager.closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    public void createProject(String projectName) {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.NewProjectScreen_Saving));
        libraryService.call(getSuccessCallback(),
                            getErrorCallBack()).createProject(projectName,
                                                              libraryPlaces.getSelectedRepository(),
                                                              getBaseURL());
    }

    private RemoteCallback<KieProject> getSuccessCallback() {
        return project -> {
            newProjectEvent.fire(new NewProjectEvent(project,
                                                     sessionInfo.getId(),
                                                     sessionInfo.getIdentity().getIdentifier()));
            hideLoadingBox();
            notifySuccess();
            goToProject(project);
            placeManager.closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
        };
    }

    private ErrorCallback<?> getErrorCallBack() {
        return (o, throwable) -> {
            hideLoadingBox();
            notificationEvent
                    .fire(new NotificationEvent(ts.getTranslation(LibraryConstants.NewProjectScreen_Error),
                                                NotificationEvent.NotificationType.ERROR));
            return false;
        };
    }

    String getBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace(GWT.getModuleName() + "/",
                                           "");
        return baseUrl;
    }

    private void goToProject(KieProject project) {
        final ProjectInfo projectInfo = new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                                                        libraryPlaces.getSelectedRepository(),
                                                        libraryInfo.getSelectedBranch(),
                                                        project);
        libraryPlaces.goToProject(projectInfo);
    }

    private void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.Project_Created),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    private void hideLoadingBox() {
        busyIndicatorView.hideBusyIndicator();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.NewProjectScreen);
    }

    @WorkbenchPartView
    public UberElement<NewProjectScreen> getView() {
        return view;
    }
}
