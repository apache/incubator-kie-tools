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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.util.KiePOMDefaultOptions;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
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
public class NewProjectWizard
        extends AbstractWizard
        implements ProjectWizard {

    private PlaceManager placeManager;
    private Event<NotificationEvent> notificationEvent;
    private POMWizardPage pomWizardPage;
    private BusyIndicatorView busyIndicatorView;
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private Caller<KieProjectService> projectServiceCaller;
    private ProjectContext context;
    private KiePOMDefaultOptions pomDefaultOptions;

    private ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private Callback<Project> projectCallback;

    boolean openEditor = true;

    //Used by ErrorCallback for "OK" operation, when New Project is to be created.
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

    public NewProjectWizard() {
    }

    @Inject
    public NewProjectWizard(final PlaceManager placeManager,
                            final Event<NotificationEvent> notificationEvent,
                            final POMWizardPage pomWizardPage,
                            final BusyIndicatorView busyIndicatorView,
                            final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                            final Caller<KieProjectService> projectServiceCaller,
                            final ProjectContext context,
                            final KiePOMDefaultOptions pomDefaultOptions) {
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.pomWizardPage = pomWizardPage;
        this.busyIndicatorView = busyIndicatorView;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.projectServiceCaller = projectServiceCaller;
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
                                     .setGroupId(context.getActiveOrganizationalUnit().getDefaultGroupId())
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
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace(GWT.getModuleName() + "/",
                                           "");
        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());
        projectServiceCaller.call(getSuccessCallback(),
                                  new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                                              errors)).newProject(context.getActiveRepositoryRoot(),
                                                                                                  pomWizardPage.getPom(),
                                                                                                  baseUrl,
                                                                                                  mode);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void start() {
        this.openEditor = true;
        this.projectCallback = null;
        super.start();
    }

    @Override
    public void start(Callback<Project> callback,
                      boolean openEditor) {
        this.projectCallback = callback;
        this.openEditor = openEditor;
        super.start();
    }

    private RemoteCallback<KieProject> getSuccessCallback() {
        return new RemoteCallback<KieProject>() {

            @Override
            public void callback(final KieProject project) {
                NewProjectWizard.super.complete();
                invokeCallback(project);
                if (openEditor) {
                    placeManager.goTo("projectScreen");
                }
                notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCreatedSuccessfully()));
                busyIndicatorView.hideBusyIndicator();
            }
        };
    }

    private void invokeCallback(Project project) {
        if (projectCallback != null) {
            projectCallback.callback(project);
            projectCallback = null;
        }
    }
}