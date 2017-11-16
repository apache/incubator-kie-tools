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
package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.ExtendedTypes;
import org.ext.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.wizard.ExamplesWizard;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectMetricsEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.library.LibraryToolbarPresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameAcceptedEvent;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

@ApplicationScoped
public class LibraryPlaces {

    public static final String LIBRARY_PERSPECTIVE = "LibraryPerspective";
    public static final String LIBRARY_SCREEN = "LibraryScreen";
    public static final String EMPTY_PROJECT_SCREEN = "EmptyProjectScreen";
    public static final String IMPORT_PROJECTS_SCREEN = "TrySamplesScreen";
    public static final String PROJECT_SCREEN = "ProjectScreen";
    public static final String PROJECT_DETAIL_SCREEN = "ProjectsDetailScreen";
    public static final String ORG_UNITS_METRICS_SCREEN = "OrgUnitsMetricsScreen";
    public static final String PROJECT_METRICS_SCREEN = "ProjectMetricsScreen";
    public static final String ORGANIZATIONAL_UNITS_SCREEN = "LibraryOrganizationalUnitsScreen";
    public static final String PROJECT_SETTINGS = "projectScreen";
    public static final String PROJECT_EXPLORER = "org.kie.guvnor.explorer";
    public static final String MESSAGES = "org.kie.workbench.common.screens.messageconsole.MessageConsole";
    public static final String REPOSITORY_STRUCTURE_SCREEN = "repositoryStructureScreen";

    public static final List<String> LIBRARY_PLACES = Collections.unmodifiableList(new ArrayList<String>(7) {{
        add(LIBRARY_SCREEN);
        add(EMPTY_PROJECT_SCREEN);
        add(IMPORT_PROJECTS_SCREEN);
        add(ORG_UNITS_METRICS_SCREEN);
        add(PROJECT_SCREEN);
        add(PROJECT_METRICS_SCREEN);
        add(PROJECT_DETAIL_SCREEN);
        add(ORGANIZATIONAL_UNITS_SCREEN);
        add(PROJECT_SETTINGS);
        add(PreferencesRootScreen.IDENTIFIER);
    }});

    private UberfireBreadcrumbs breadcrumbs;

    private TranslationService ts;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Event<ProjectMetricsEvent> projectMetricsEvent;

    private Event<AssetDetailEvent> assetDetailEvent;

    private ResourceUtils resourceUtils;

    private Caller<LibraryService> libraryService;

    private PlaceManager placeManager;

    private LibraryPerspective libraryPerspective;

    private ProjectContext projectContext;

    private LibraryToolbarPresenter libraryToolbar;

    private AuthoringWorkbenchDocks docks;

    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    private Event<NotificationEvent> notificationEvent;

    private ManagedInstance<ExamplesWizard> examplesWizards;

    private TranslationUtils translationUtils;

    private Caller<VFSService> vfsService;

    private Caller<ExplorerService> explorerService;

    private ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    private boolean docksReady = false;

    private boolean docksHidden = true;

    private Project lastViewedProject = null;

    private boolean closingLibraryPlaces = false;

    @Inject
    public LibraryPlaces(final UberfireBreadcrumbs breadcrumbs,
                         final TranslationService ts,
                         final Event<ProjectDetailEvent> projectDetailEvent,
                         final Event<ProjectMetricsEvent> projectMetricsEvent,
                         final Event<AssetDetailEvent> assetDetailEvent,
                         final ResourceUtils resourceUtils,
                         final Caller<LibraryService> libraryService,
                         final PlaceManager placeManager,
                         final LibraryPerspective libraryPerspective,
                         final ProjectContext projectContext,
                         final LibraryToolbarPresenter libraryToolbar,
                         final AuthoringWorkbenchDocks docks,
                         final Event<ProjectContextChangeEvent> projectContextChangeEvent,
                         final Event<NotificationEvent> notificationEvent,
                         final ManagedInstance<ExamplesWizard> examplesWizards,
                         final TranslationUtils translationUtils,
                         final Caller<VFSService> vfsService,
                         final Caller<ExplorerService> explorerService,
                         final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier,
                         final Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent) {
        this.breadcrumbs = breadcrumbs;
        this.ts = ts;
        this.projectDetailEvent = projectDetailEvent;
        this.projectMetricsEvent = projectMetricsEvent;
        this.assetDetailEvent = assetDetailEvent;
        this.resourceUtils = resourceUtils;
        this.libraryService = libraryService;
        this.placeManager = placeManager;
        this.libraryPerspective = libraryPerspective;
        this.projectContext = projectContext;
        this.libraryToolbar = libraryToolbar;
        this.docks = docks;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.notificationEvent = notificationEvent;
        this.examplesWizards = examplesWizards;
        this.translationUtils = translationUtils;
        this.vfsService = vfsService;
        this.explorerService = explorerService;
        this.projectScopedResolutionStrategySupplier = projectScopedResolutionStrategySupplier;
        this.preferencesCentralInitializationEvent = preferencesCentralInitializationEvent;
    }

    public ProjectInfo getProjectInfo() {
        return new ProjectInfo(projectContext.getActiveOrganizationalUnit(),
                               projectContext.getActiveRepository(),
                               projectContext.getActiveBranch(),
                               projectContext.getActiveProject());
    }

    public void onSelectPlaceEvent(@Observes final PlaceGainFocusEvent placeGainFocusEvent) {
        if (isLibraryPerspectiveOpen() && !closingLibraryPlaces) {
            final ProjectInfo projectInfo = getProjectInfo();
            final PlaceRequest place = placeGainFocusEvent.getPlace();

            if (place instanceof PathPlaceRequest) {
                final PathPlaceRequest pathPlaceRequest = (PathPlaceRequest) place;
                setupLibraryBreadCrumbsForAsset(projectInfo,
                                                pathPlaceRequest.getPath());
                showDocks();
            } else if (!place.getIdentifier().equals(MESSAGES) && isLibraryPlace(place)) {
                hideDocks();
                if (place.getIdentifier().equals(PROJECT_SETTINGS)) {
                    setupLibraryBreadCrumbsForAsset(projectInfo,
                                                    null);
                } else if (projectInfo.getProject() != null
                        && (place.getIdentifier().equals(LibraryPlaces.PROJECT_SCREEN)
                        || place.getIdentifier().equals(LibraryPlaces.EMPTY_PROJECT_SCREEN))) {
                    setupLibraryBreadCrumbsForProject(projectInfo);
                } else if (place.getIdentifier().equals(LibraryPlaces.LIBRARY_SCREEN)) {
                    setupLibraryBreadCrumbs();
                }
            }
        }
    }

    public void hideDocks() {
        if (!docksHidden) {
            docks.hide();
            docksHidden = true;
        }
    }

    public void showDocks() {
        if (docksHidden) {
            if (!docksReady) {
                docks.setup(LibraryPlaces.LIBRARY_PERSPECTIVE,
                            new DefaultPlaceRequest(PROJECT_EXPLORER));
                docksReady = true;
            }
            docks.show();
            docksHidden = false;
        }
    }

    private boolean isLibraryPlace(final PlaceRequest place) {
        return LIBRARY_PLACES.contains(place.getIdentifier());
    }

    public void newResourceCreated(@Observes final NewResourceSuccessEvent newResourceSuccessEvent) {
        if (isLibraryPerspectiveOpen()) {
            assetDetailEvent.fire(new AssetDetailEvent(getProjectInfo(),
                                                       newResourceSuccessEvent.getPath()));
        }
    }

    public void assetRenamedAccepted(@Observes final ConcurrentRenameAcceptedEvent concurrentRenameAcceptedEvent) {
        if (isLibraryPerspectiveOpen()) {
            final ProjectInfo projectInfo = getProjectInfo();
            final ObservablePath path = concurrentRenameAcceptedEvent.getPath();
            goToAsset(projectInfo,
                      path);
            setupLibraryBreadCrumbsForAsset(projectInfo,
                                            path);
        }
    }

    public void projectDeleted(@Observes final DeleteProjectEvent deleteProjectEvent) {
        if (isLibraryPerspectiveOpen()) {
            if (deleteProjectEvent.getProject().equals(lastViewedProject)) {
                closeAllPlaces();
                goToLibrary();
                notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.ProjectDeleted),
                                                             NotificationEvent.NotificationType.DEFAULT));
            }
        }
    }

    public void projectRenamed(@Observes final RenameProjectEvent renameProjectEvent) {
        if (isLibraryPerspectiveOpen()) {
            final Project activeProject = projectContext.getActiveProject();

            if (renameProjectEvent.getOldProject().equals(activeProject)) {
                setupLibraryBreadCrumbsForAsset(new ProjectInfo(projectContext.getActiveOrganizationalUnit(),
                                                                projectContext.getActiveRepository(),
                                                                projectContext.getActiveBranch(),
                                                                renameProjectEvent.getNewProject()),
                                                null);
                lastViewedProject = renameProjectEvent.getNewProject();
            }
        }
    }

    public void assetSelected(@Observes final AssetDetailEvent assetDetails) {
        goToAsset(assetDetails.getProjectInfo(),
                  assetDetails.getPath());
    }

    public void projectContextChange() {
        final ProjectInfo projectInfo = new ProjectInfo(projectContext.getActiveOrganizationalUnit(),
                                                        projectContext.getActiveRepository(),
                                                        projectContext.getActiveBranch(),
                                                        projectContext.getActiveProject());

        if (isLibraryPerspectiveOpen() && projectContext.getActiveRepository() != null) {
            if ((libraryToolbar.getSelectedRepository() != null && !libraryToolbar.getSelectedRepository().equals(projectContext.getActiveRepository()))
                    || (libraryToolbar.getSelectedBranch() != null && !libraryToolbar.getSelectedBranch().equals(projectContext.getActiveBranch()))) {
                libraryToolbar.setSelectedInfo(projectContext.getActiveOrganizationalUnit(),
                                               projectContext.getActiveRepository(),
                                               () -> {
                                                   if (projectContext.getActiveProject() != null) {
                                                       goToProject(projectInfo,
                                                                   false);
                                                   }
                                               });
            } else if (projectContext.getActiveProject() != null && !projectContext.getActiveProject().equals(lastViewedProject)) {
                goToProject(projectInfo,
                            false);
            }
        }
    }

    private boolean isLibraryPerspectiveOpen() {
        return placeManager.getStatus(LIBRARY_PERSPECTIVE).equals(PlaceStatus.OPEN)
                || placeManager.getStatus(getLibraryPlaceRequestWithoutRefresh()).equals(PlaceStatus.OPEN);
    }

    public void onPreferencesSave(@Observes PreferencesCentralSaveEvent event) {
        if (isLibraryPerspectiveOpen()) {
            goToProject(getProjectInfo());
        }
    }

    public void onPreferencesCancel(@Observes PreferencesCentralUndoChangesEvent event) {
        if (isLibraryPerspectiveOpen()) {
            goToProject(getProjectInfo());
        }
    }

    public void onSocialFileSelected(@Observes final SocialFileSelectedEvent event) {
        vfsService.call(new RemoteCallback<Path>() {
            @Override
            public void callback(Path path) {
                explorerService.call(new RemoteCallback<URIStructureExplorerModel>() {
                    @Override
                    public void callback(final URIStructureExplorerModel model) {
                        openBestSuitedScreen(event.getEventType(),
                                             path,
                                             new ProjectInfo(model.getOrganizationalUnit(),
                                                             model.getRepository(),
                                                             model.getRepository().getDefaultBranch(),
                                                             model.getProject()));
                    }
                }).getURIStructureExplorerModel(path);
            }
        }).get(event.getUri());
    }

    private void openBestSuitedScreen(final String eventType,
                                      final Path path,
                                      final ProjectInfo projectInfo) {
        final PlaceRequest libraryPerspective = getLibraryPlaceRequestWithoutRefresh();

        if (isRepositoryEvent(eventType)) {
            placeManager.goTo(REPOSITORY_STRUCTURE_SCREEN);
        } else if (isProjectEvent(eventType)) {
            placeManager.goTo(libraryPerspective);
            goToProject(projectInfo);
        } else if (path != null) {
            placeManager.goTo(libraryPerspective);
            goToProject(projectInfo,
                        true,
                        () -> goToAsset(projectInfo,
                                        path));
        }
    }

    PlaceRequest getLibraryPlaceRequestWithoutRefresh() {
        final Map<String, String> params = new HashMap<>();
        params.put("refresh",
                   "false");
        return new DefaultPlaceRequest(LIBRARY_PERSPECTIVE,
                                       params);
    }

    private boolean isRepositoryEvent(String eventType) {
        if (eventType == null || eventType.isEmpty()) {
            return false;
        }

        if (ExtendedTypes.NEW_REPOSITORY_EVENT.name().equals(eventType)
                || AssetManagementEventTypes.BRANCH_CREATED.name().equals(eventType)
                || AssetManagementEventTypes.REPOSITORY_CHANGE.name().equals(eventType)) {
            return true;
        }

        return false;
    }

    private boolean isProjectEvent(final String eventType) {
        return ProjectEventType.NEW_PROJECT.name().equals(eventType);
    }

    public void setupToolBar() {
        breadcrumbs.clearBreadcrumbsAndToolBars(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addToolbar(LibraryPlaces.LIBRARY_PERSPECTIVE,
                               libraryToolbar.getView().getElement());
    }

    public void setupLibraryBreadCrumbsForOrganizationUnits() {
        breadcrumbs.clearBreadcrumbsAndToolBars(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
    }

    public void setupLibraryBreadCrumbs() {
        setupToolBar();
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
    }

    public void setupLibraryBreadCrumbsForTrySamples() {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.TrySamples),
                                  () -> goToTrySamples());
    }

    public void setupLibraryBreadCrumbsForImportProjects(final String repositoryUrl) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.ImportProjects),
                                  () -> goToImportProjects(repositoryUrl));
    }

    public void setupLibraryBreadCrumbsForProject(final ProjectInfo projectInfo) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  projectInfo.getProject().getProjectName(),
                                  () -> goToProject(projectInfo));
    }

    public void setupLibraryBreadCrumbsForProjectMetrics(final ProjectInfo projectInfo) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  projectInfo.getProject().getProjectName(),
                                  () -> goToProject(projectInfo));
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getProjectMetrics(),
                                  () -> goToProjectMetrics(projectInfo));
    }

    public void setupLibraryBreadCrumbsForOrgUnitsMetrics() {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrgUnitsMetrics(),
                                  () -> goToOrgUnitsMetrics());
    }

    public void setupLibraryBreadCrumbsForAsset(final ProjectInfo projectInfo,
                                                final Path path) {
        String assetName;
        if (path != null) {
            assetName = resourceUtils.getBaseFileName(path);
        } else {
            assetName = ts.format(LibraryConstants.Settings);
        }

        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  projectInfo.getProject().getProjectName(),
                                  () -> goToProject(projectInfo));
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  assetName,
                                  () -> goToAsset(projectInfo,
                                                  path));
    }

    public void setupLibraryBreadCrumbsForPreferences(final ProjectInfo projectInfo) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  getSelectedOrganizationalUnit().getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  projectInfo.getProject().getProjectName(),
                                  () -> goToProject(projectInfo));
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.Preferences),
                                  () -> goToPreferences());
    }

    public void refresh(final Command callback) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        translationUtils.refresh(() -> {
            libraryToolbar.init(() -> {
                setupToolBar();
                if (callback != null) {
                    callback.execute();
                }
            });
        });
    }

    public void goToOrganizationalUnits() {
        if (closeAllPlacesOrNothing()) {
            final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.ORGANIZATIONAL_UNITS_SCREEN);
            final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
            part.setSelectable(false);
            placeManager.goTo(part,
                              libraryPerspective.getRootPanel());
            setupLibraryBreadCrumbsForOrganizationUnits();
        }
    }

    public void goToLibrary() {
        goToLibrary(null);
    }

    public void goToLibrary(final Command callback) {
        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        closeLibraryPlaces();
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());

        setupLibraryBreadCrumbs();

        hideDocks();

        if (callback != null) {
            callback.execute();
        } else {
            projectContextChangeEvent.fire(new ProjectContextChangeEvent(getSelectedOrganizationalUnit(),
                                                                         getSelectedRepository(),
                                                                         getSelectedBranch()));
        }
    }

    public void goToProject(final ProjectInfo projectInfo) {
        goToProject(projectInfo,
                    true);
    }

    public void goToProject(final ProjectInfo projectInfo,
                            final boolean fireProjectContextChangeEvent) {
        goToProject(projectInfo,
                    fireProjectContextChangeEvent,
                    null);
    }

    public void goToProject(final ProjectInfo projectInfo,
                            final boolean fireProjectContextChangeEvent,
                            final Command callback) {
        libraryService.call(hasAssets -> {
            final PlaceRequest projectScreen = new ConditionalPlaceRequest(LibraryPlaces.PROJECT_SCREEN)
                    .when(p -> (Boolean) hasAssets)
                    .orElse(new DefaultPlaceRequest(LibraryPlaces.EMPTY_PROJECT_SCREEN));
            final PartDefinitionImpl part = new PartDefinitionImpl(projectScreen);
            part.setSelectable(false);

            boolean goToProject = true;
            if (!projectInfo.getProject().equals(lastViewedProject)) {
                goToProject = closeAllPlacesOrNothing();
            }

            if (goToProject) {
                closeLibraryPlaces();
                hideDocks();
                lastViewedProject = projectInfo.getProject();
                if (fireProjectContextChangeEvent) {
                    projectContextChangeEvent.fire(new ProjectContextChangeEvent(projectInfo.getOrganizationalUnit(),
                                                                                 projectInfo.getRepository(),
                                                                                 projectInfo.getBranch(),
                                                                                 projectInfo.getProject()));
                }

                placeManager.goTo(part,
                                  libraryPerspective.getRootPanel());

                setupLibraryBreadCrumbsForProject(projectInfo);
                projectDetailEvent.fire(new ProjectDetailEvent(projectInfo));

                if (callback != null) {
                    callback.execute();
                }
            }
        }).hasAssets(projectInfo.getProject());
    }

    public void goToOrgUnitsMetrics() {
        final PlaceRequest metricsScreen = new DefaultPlaceRequest(LibraryPlaces.ORG_UNITS_METRICS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(metricsScreen);
        part.setSelectable(false);
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
        setupLibraryBreadCrumbsForOrgUnitsMetrics();
    }

    public void goToProjectMetrics(final ProjectInfo projectInfo) {
        final PlaceRequest metricsScreen = new DefaultPlaceRequest(LibraryPlaces.PROJECT_METRICS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(metricsScreen);
        part.setSelectable(false);
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
        setupLibraryBreadCrumbsForProjectMetrics(projectInfo);
        projectMetricsEvent.fire(new ProjectMetricsEvent(projectInfo));
    }

    public void goToAsset(final ProjectInfo projectInfo,
                          final Path path) {
        final PlaceRequest placeRequest = generatePlaceRequest(path);
        placeManager.goTo(placeRequest);

        if (path != null) {
            final ObservablePath observablePath = ((PathPlaceRequest) placeRequest).getPath();
            observablePath.onRename(() -> setupLibraryBreadCrumbsForAsset(projectInfo,
                                                                          observablePath));
        }
    }

    public void goToTrySamples() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.IMPORT_PROJECTS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        closeLibraryPlaces();
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
        setupLibraryBreadCrumbsForTrySamples();
    }

    public void goToImportProjects(final String repositoryUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("title",
                   ts.getTranslation(LibraryConstants.ImportProjects));
        params.put("repositoryUrl",
                   repositoryUrl);
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.IMPORT_PROJECTS_SCREEN,
                                                                         params);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        closeLibraryPlaces();
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
        setupLibraryBreadCrumbsForImportProjects(repositoryUrl);
    }

    public void goToSettings(final ProjectInfo projectInfo) {
        assetDetailEvent.fire(new AssetDetailEvent(projectInfo,
                                                   null));
    }

    public void goToImportProjectWizard() {
        final String organizationalUnitName = projectContext.getActiveOrganizationalUnit().getName();
        final String repositoryAlias = projectContext.getActiveRepository().getAlias();

        final ExamplesWizard examplesWizard = examplesWizards.get();
        examplesWizard.start();
        examplesWizard.setDefaultTargetOrganizationalUnit(organizationalUnitName);
        examplesWizard.setDefaultTargetRepository(repositoryAlias);
    }

    public void goToMessages() {
        placeManager.goTo(MESSAGES);
    }

    public void goToPreferences() {
        final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy = projectScopedResolutionStrategySupplier.get();
        final PreferencesCentralInitializationEvent initEvent = new PreferencesCentralInitializationEvent("ProjectPreferences",
                                                                                                          customScopeResolutionStrategy,
                                                                                                          null);

        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(PreferencesRootScreen.IDENTIFIER);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());

        preferencesCentralInitializationEvent.fire(initEvent);
        setupLibraryBreadCrumbsForPreferences(getProjectInfo());
    }

    public OrganizationalUnit getSelectedOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit();
    }

    public Repository getSelectedRepository() {
        return libraryToolbar.getSelectedRepository();
    }

    public String getSelectedBranch() {
        return libraryToolbar.getSelectedBranch();
    }

    PlaceRequest generatePlaceRequest(final Path path) {
        if (path == null) {
            return new DefaultPlaceRequest(PROJECT_SETTINGS);
        }

        return createPathPlaceRequest(path);
    }

    PathPlaceRequest createPathPlaceRequest(final Path path) {
        return new PathPlaceRequest(path);
    }

    void closeLibraryPlaces() {
        closingLibraryPlaces = true;
        LIBRARY_PLACES.forEach(place -> placeManager.closePlace(place));
        closingLibraryPlaces = false;
    }

    boolean closeAllPlacesOrNothing() {
        closingLibraryPlaces = true;
        final boolean placesClosed = placeManager.closeAllPlacesOrNothing();
        closingLibraryPlaces = false;

        return placesClosed;
    }

    void closeAllPlaces() {
        closingLibraryPlaces = true;
        placeManager.closeAllPlaces();
        closingLibraryPlaces = false;
    }
}
