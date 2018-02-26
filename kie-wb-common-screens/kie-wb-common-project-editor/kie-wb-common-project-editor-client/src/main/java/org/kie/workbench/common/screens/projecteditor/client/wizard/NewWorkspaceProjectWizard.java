/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.model.WorkspaceProjectWizard;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.util.KiePOMDefaultOptions;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewWorkspaceProjectWizard
        extends AbstractWizard
        implements WorkspaceProjectWizard {

    boolean openEditor = true;
    private Caller<WorkspaceProjectService> projectService;
    private PlaceManager placeManager;
    private Event<NotificationEvent> notificationEvent;
    private POMWizardPage pomWizardPage;
    private BusyIndicatorView busyIndicatorView;
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private WorkspaceProjectContext context;
    private KiePOMDefaultOptions pomDefaultOptions;
    private ArrayList<WizardPage> pages = new ArrayList<>();
    private Callback<WorkspaceProject> moduleCallback;
    //Used by ErrorCallback for "OK" operation, when New Module is to be created.
    private Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
        put(GAVAlreadyExistsException.class,
            new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                @Override
                public void execute(final Throwable parameter) {
                    busyIndicatorView.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(pomWizardPage.getPom().getGav(),
                                                            ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                            new Command() {
                                                                @Override
                                                                public void execute() {
                                                                    conflictingRepositoriesPopup.hide();
                                                                    onComplete(DeploymentMode.FORCED);
                                                                }
                                                            });
                    conflictingRepositoriesPopup.show();
                }
            });
    }};

    public NewWorkspaceProjectWizard() {
    }

    @Inject
    public NewWorkspaceProjectWizard(final Caller<WorkspaceProjectService> projectService,
                                     final PlaceManager placeManager,
                                     final Event<NotificationEvent> notificationEvent,
                                     final POMWizardPage pomWizardPage,
                                     final BusyIndicatorView busyIndicatorView,
                                     final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                     final WorkspaceProjectContext context,
                                     final KiePOMDefaultOptions pomDefaultOptions) {
        this.projectService = projectService;
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.pomWizardPage = pomWizardPage;
        this.busyIndicatorView = busyIndicatorView;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.context = context;
        this.pomDefaultOptions = pomDefaultOptions;
    }

    @PostConstruct
    public void setupPages() {
        pages.add(pomWizardPage);
    }

    @Override
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.NewProject();
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget(int pageNumber) {
        return pomWizardPage.asWidget();
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        //We only have one page; this is simple!
        pomWizardPage.isComplete(callback);
    }

    @Override
    public void initialise() {
        pomWizardPage.setPom(new POMBuilder()
                                             .setGroupId(context.getActiveOrganizationalUnit()
                                                                .map(ou -> ou.getDefaultGroupId())
                                                                .orElseThrow(() -> new IllegalStateException("Cannot get default group id when no organizational unit is active.")))
                                             .build());
    }

    @Override
    public void initialise(final POM pom) {
        final POMBuilder pomBuilder = new POMBuilder(pom);

        pomBuilder.setBuildPlugins(pomDefaultOptions.getBuildPlugins());

        pomWizardPage.setPom(pomBuilder.build());
    }

    @Override
    public void complete() {
        onComplete(DeploymentMode.VALIDATED);
    }

    private void onComplete(final DeploymentMode mode) {
        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());

        projectService.call(getSuccessCallback(),
                            new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                                        errors)).newProject(context.getActiveOrganizationalUnit()
                                                                                                   .orElseThrow(() -> new IllegalStateException("Cannot call project service to create new project when no organizational unit is active.")),
                                                                                            pomWizardPage.getPom(),
                                                                                            mode);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void start() {
        this.openEditor = true;
        this.moduleCallback = null;
        super.start();
    }

    @Override
    public void start(final Callback<WorkspaceProject> callback,
                      final boolean openEditor) {
        this.moduleCallback = callback;
        this.openEditor = openEditor;
        super.start();
    }

    private RemoteCallback<WorkspaceProject> getSuccessCallback() {
        return new RemoteCallback<WorkspaceProject>() {

            @Override
            public void callback(final WorkspaceProject project) {
                NewWorkspaceProjectWizard.super.complete();
                invokeCallback(project);
                if (openEditor) {
                    placeManager.goTo("ProjectSettings");
                }
                notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCreatedSuccessfully()));
                busyIndicatorView.hideBusyIndicator();
            }
        };
    }

    private void invokeCallback(final WorkspaceProject project) {
        if (moduleCallback != null) {
            moduleCallback.callback(project);
            moduleCallback = null;
        }
    }
}