/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.assets.events.UpdatedAssetsEvent;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.delete.DeleteProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.rename.RenameProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ProjectScreen {

    private Elemental2DomUtil elemental2DomUtil;
    protected WorkspaceProject workspaceProject;

    public interface View extends UberElemental<ProjectScreen>,
                                  BuildExecutor.View {

        void addMainAction(IsElement action);

        void setAssetsCount(int count);

        void setContributorsCount(int count);

        void setContent(HTMLElement content);

        void setTitle(String projectName);

        void setEditContributorsVisible(boolean visible);

        void setAddAssetVisible(boolean visible);

        void setImportAssetVisible(boolean visible);

        void setBuildEnabled(boolean enabled);

        void setDeployEnabled(boolean enabled);

        void setDeleteProjectVisible(boolean visible);

        String getLoadingMessage();

        String getItemSuccessfullyDuplicatedMessage();

        String getReimportSuccessfulMessage();
    }

    private final LibraryPlaces libraryPlaces;

    private AssetsScreen assetsScreen;
    private ContributorsListPresenter contributorsListScreen;
    private ProjectMetricsScreen projectMetricsScreen;
    private ProjectController projectController;
    private SettingsPresenter settingsPresenter;
    private OrganizationalUnitController organizationalUnitController;
    private final NewFileUploader newFileUploader;
    private final NewResourcePresenter newResourcePresenter;
    private BuildExecutor buildExecutor;
    private ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenter;
    private ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreen;
    private ManagedInstance<RenameProjectPopUpScreen> renameProjectPopUpScreen;
    private Caller<LibraryService> libraryService;
    private ProjectScreen.View view;
    private Caller<ProjectScreenService> projectScreenService;
    private CopyPopUpPresenter copyPopUpPresenter;
    private ProjectNameValidator projectNameValidator;
    private Promises promises;
    private Event<NotificationEvent> notificationEvent;
    private ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter;

    @Inject
    public ProjectScreen(final View view,
                         final LibraryPlaces libraryPlaces,
                         final AssetsScreen assetsScreen,
                         final ContributorsListPresenter contributorsListScreen,
                         final ProjectMetricsScreen projectMetricsScreen,
                         final ProjectController projectController,
                         final SettingsPresenter settingsPresenter,
                         final OrganizationalUnitController organizationalUnitController,
                         final NewFileUploader newFileUploader,
                         final NewResourcePresenter newResourcePresenter,
                         final BuildExecutor buildExecutor,
                         final ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenter,
                         final ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreen,
                         final ManagedInstance<RenameProjectPopUpScreen> renameProjectPopUpScreen,
                         final Caller<LibraryService> libraryService,
                         final Caller<ProjectScreenService> projectScreenService,
                         final CopyPopUpPresenter copyPopUpPresenter,
                         final ProjectNameValidator projectNameValidator,
                         final Promises promises,
                         final Event<NotificationEvent> notificationEvent,
                         final ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.assetsScreen = assetsScreen;
        this.contributorsListScreen = contributorsListScreen;
        this.projectMetricsScreen = projectMetricsScreen;
        this.projectController = projectController;
        this.settingsPresenter = settingsPresenter;
        this.organizationalUnitController = organizationalUnitController;
        this.newFileUploader = newFileUploader;
        this.newResourcePresenter = newResourcePresenter;
        this.buildExecutor = buildExecutor;
        this.editContributorsPopUpPresenter = editContributorsPopUpPresenter;
        this.deleteProjectPopUpScreen = deleteProjectPopUpScreen;
        this.renameProjectPopUpScreen = renameProjectPopUpScreen;
        this.libraryService = libraryService;
        this.projectScreenService = projectScreenService;
        this.copyPopUpPresenter = copyPopUpPresenter;
        this.projectNameValidator = projectNameValidator;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
        this.viewHideAlertsButtonPresenter = viewHideAlertsButtonPresenter;
        this.elemental2DomUtil = new Elemental2DomUtil();
    }

    @PostConstruct
    public void initialize() {
        this.workspaceProject = this.libraryPlaces.getActiveWorkspace();
        this.view.init(this);
        this.buildExecutor.init(this.view);
        this.view.setTitle(libraryPlaces.getActiveWorkspace().getName());
        this.view.addMainAction(viewHideAlertsButtonPresenter.getView());
        this.resolveContributorsCount();
        this.resolveAssetsCount();
        this.showAssets();

        this.view.setEditContributorsVisible(this.canEditContributors());
        this.view.setAddAssetVisible(this.userCanUpdateProject());
        this.view.setImportAssetVisible(this.userCanUpdateProject());
        this.view.setImportAssetVisible(this.userCanUpdateProject());
        this.view.setBuildEnabled(this.userCanBuildProject());
        this.view.setDeployEnabled(this.userCanBuildProject());
        this.view.setDeleteProjectVisible(this.userCanDeleteRepository());

        newFileUploader.acceptContext(new Callback<Boolean, Void>() {
            @Override
            public void onFailure(Void reason) {
                view.setImportAssetVisible(false);
            }

            @Override
            public void onSuccess(Boolean result) {
                view.setImportAssetVisible(result);
            }
        });
    }

    @OnMayClose
    public boolean onMayClose() {
        return settingsPresenter.mayClose();
    }

    public void setAssetsCount(Integer assetsCount) {
        this.view.setAssetsCount(assetsCount);
    }

    public void onAddAsset(@Observes NewResourceSuccessEvent event) {
        resolveAssetsCount();
    }

    public void onAssetsUpdated(@Observes UpdatedAssetsEvent event) {
        resolveAssetsCount();
    }

    public void onContributorsUpdated(@Observes AfterEditOrganizationalUnitEvent event) {
        resolveContributorsCount();
    }

    public void changeProjectAndTitleWhenContextChange(@Observes final WorkspaceProjectContextChangeEvent current) {
        if (current.getWorkspaceProject() != null) {
            this.workspaceProject = current.getWorkspaceProject();
            this.view.setTitle(workspaceProject.getName());
        }
    }

    private void resolveContributorsCount() {
        this.view.setContributorsCount(this.contributorsListScreen.getContributorsCount());
    }

    private void resolveAssetsCount() {
        this.libraryService.call((Integer numberOfAssets) -> this.setAssetsCount(numberOfAssets))
                .getNumberOfAssets(this.workspaceProject);
    }

    public void showAssets() {
        this.view.setContent(this.assetsScreen.getView().getElement());
    }

    public void showMetrics() {
        this.projectMetricsScreen.onStartup(workspaceProject);
        this.view.setContent(elemental2DomUtil.asHTMLElement(this.projectMetricsScreen.getView().getElement()));
    }

    public void showContributors() {
        this.view.setContent(elemental2DomUtil.asHTMLElement(this.contributorsListScreen.getView().getElement()));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return this.libraryPlaces.getActiveWorkspace().getName();
    }

    public void delete() {
        if (userCanDeleteRepository()) {
            final DeleteProjectPopUpScreen popUp = deleteProjectPopUpScreen.get();
            popUp.show(this.workspaceProject);
        }
    }

    public void addAsset() {
        if (userCanUpdateProject()) {
            this.libraryPlaces.goToAddAsset();
        }
    }

    public void importAsset() {
        if (userCanUpdateProject()) {
            newFileUploader.getCommand(newResourcePresenter).execute();
        }
    }

    public void showSettings() {
        if (userCanUpdateProject()) {
            settingsPresenter.setupUsingCurrentSection().then(i -> {
                SettingsPresenter.View settingsView = this.settingsPresenter.getView();
                this.view.setContent(settingsView.getElement());
                return promises.resolve();
            });
        }
    }

    public void rename() {
        if (userCanUpdateProject()) {
            final RenameProjectPopUpScreen popUp = renameProjectPopUpScreen.get();
            popUp.show(this.workspaceProject);
        }
    }

    public void editContributors() {
        if (this.canEditContributors()) {
            final EditContributorsPopUpPresenter popUp = editContributorsPopUpPresenter.get();
            popUp.show(this.workspaceProject.getOrganizationalUnit());
        }
    }

    public void duplicate() {
        if (this.userCanCreateProjects()) {
            copyPopUpPresenter.show(
                    workspaceProject.getRootPath(),
                    projectNameValidator,
                    getDuplicateCommand());
        }
    }

    CommandWithFileNameAndCommitMessage getDuplicateCommand() {
        return details -> {
            copyPopUpPresenter.getView().hide();

            view.showBusyIndicator(view.getLoadingMessage());

            promises.promisify(projectScreenService, s -> {
                s.copy(workspaceProject, details.getNewFileName());
            }).then(i -> {
                view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(view.getItemSuccessfullyDuplicatedMessage(),
                                                             NotificationEvent.NotificationType.SUCCESS));
                return promises.resolve();
            }).catch_(this::onError);
        };
    }

    public void reimport() {
        if (this.userCanUpdateProject()) {
            final Path pomXMLPath = workspaceProject.getMainModule().getPomXMLPath();
            view.showBusyIndicator(view.getLoadingMessage());

            promises.promisify(projectScreenService, s -> {
                s.reImport(pomXMLPath);
            }).then(i -> {
                view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(view.getReimportSuccessfulMessage(),
                                                             NotificationEvent.NotificationType.SUCCESS));
                return promises.resolve();
            }).catch_(this::onError);
        }
    }

    private Promise<Object> onError(final Object object) {
        return promises.catchOrExecute(object, e -> {
            new HasBusyIndicatorDefaultErrorCallback(view).error(null, e);
            return promises.resolve();
        }, (final Promises.Error<Message> e) -> {
            new HasBusyIndicatorDefaultErrorCallback(view).error(e.getObject(), e.getThrowable());
            return promises.resolve();
        });
    }

    public void build() {
        if (this.userCanBuildProject()) {
            this.buildExecutor.triggerBuild();
        }
    }

    public void deploy() {
        if (this.userCanBuildProject()) {
            this.buildExecutor.triggerBuildAndDeploy();
        }
    }

    public boolean canEditContributors() {
        return this.organizationalUnitController.canUpdateOrgUnit(this.workspaceProject.getOrganizationalUnit());
    }

    public boolean userCanDeleteRepository() {
        return projectController.canDeleteProject(this.workspaceProject);
    }

    public boolean userCanBuildProject() {
        return workspaceProject.getMainModule() != null && projectController.canBuildProject(this.workspaceProject);
    }

    public boolean userCanUpdateProject() {
        return workspaceProject.getMainModule() != null && projectController.canUpdateProject(this.workspaceProject);
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects();
    }

    @WorkbenchPartView
    public ProjectScreen.View getView() {
        return view;
    }
}
