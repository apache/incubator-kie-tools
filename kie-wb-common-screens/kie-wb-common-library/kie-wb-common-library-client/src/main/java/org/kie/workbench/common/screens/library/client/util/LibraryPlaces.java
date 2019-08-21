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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeHandler;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.Remote;
import org.kie.workbench.common.screens.library.api.Routed;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportProjectsSetupEvent;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.importrepository.Source;
import org.kie.workbench.common.screens.library.client.screens.project.close.CloseUnsavedProjectAssetsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.breadcrumb.LibraryBreadcrumbs;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.slf4j.Logger;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameAcceptedEvent;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

@ApplicationScoped
public class LibraryPlaces implements WorkspaceProjectContextChangeHandler {

    public static final String LIBRARY_PERSPECTIVE = "LibraryPerspective";
    public static final String LIBRARY_SCREEN = "LibraryScreen";
    public static final String PROJECT_SCREEN = "ProjectScreen";
    public static final String IMPORT_PROJECTS_SCREEN = "ImportProjectsScreen";
    public static final String IMPORT_SAMPLE_PROJECTS_SCREEN = "TrySamplesScreen";
    public static final String PROJECT_DETAIL_SCREEN = "ProjectsDetailScreen";
    public static final String ORG_UNITS_METRICS_SCREEN = "OrgUnitsMetricsScreen";
    public static final String PROJECT_METRICS_SCREEN = "ProjectMetricsScreen";
    public static final String ORGANIZATIONAL_UNITS_SCREEN = "LibraryOrganizationalUnitsScreen";
    public static final String PROJECT_SETTINGS = "ProjectSettings";
    public static final String PROJECT_EXPLORER = "org.kie.guvnor.explorer";
    public static final String ALERTS = MessageConsoleScreen.ALERTS;
    public static final String REPOSITORY_STRUCTURE_SCREEN = "repositoryStructureScreen";
    public static final String ADD_ASSET_SCREEN = "AddAssetsScreen";

    public static final List<String> LIBRARY_PLACES = Arrays.asList(
            LIBRARY_SCREEN,
            ORG_UNITS_METRICS_SCREEN,
            PROJECT_SCREEN,
            PROJECT_METRICS_SCREEN,
            PROJECT_DETAIL_SCREEN,
            ORGANIZATIONAL_UNITS_SCREEN,
            PROJECT_SETTINGS,
            ADD_ASSET_SCREEN,
            IMPORT_PROJECTS_SCREEN,
            IMPORT_SAMPLE_PROJECTS_SCREEN,
            PreferencesRootScreen.IDENTIFIER
    );

    private UberfireBreadcrumbs breadcrumbs;

    private TranslationService ts;

    private Event<AssetDetailEvent> assetDetailEvent;

    private Caller<LibraryService> libraryService;

    private Caller<WorkspaceProjectService> projectService;

    private Caller<KieModuleService> moduleService;

    private PlaceManager placeManager;

    private LibraryPerspective libraryPerspective;

    private WorkspaceProjectContext projectContext;

    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    private Event<NotificationEvent> notificationEvent;

    private TranslationUtils translationUtils;

    private Caller<VFSService> vfsService;

    private ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters;

    private Event<ProjectAssetListUpdated> assetListUpdatedEvent;

    private CloseUnsavedProjectAssetsPopUpPresenter closeUnsavedProjectAssetsPopUpPresenter;

    private Event<ImportProjectsSetupEvent> importProjectsSetupEvent;

    private LibraryBreadcrumbs libraryBreadcrumbs;

    private SessionInfo sessionInfo;

    private Caller<RepositoryService> repositoryService;

    private OrganizationalUnitController organizationalUnitController;

    private Promises promises;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Logger logger;

    private boolean closingLibraryPlaces = false;

    public LibraryPlaces() {
    }

    @Inject
    public LibraryPlaces(final UberfireBreadcrumbs breadcrumbs,
                         final TranslationService ts,
                         final Event<AssetDetailEvent> assetDetailEvent,
                         final Caller<LibraryService> libraryService,
                         final Caller<WorkspaceProjectService> projectService,
                         final Caller<KieModuleService> moduleService,
                         final PlaceManager placeManager,
                         final WorkspaceProjectContext projectContext,
                         final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                         final Event<NotificationEvent> notificationEvent,
                         final TranslationUtils translationUtils,
                         final Caller<VFSService> vfsService,
                         final ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters,
                         final @Routed Event<ProjectAssetListUpdated> assetListUpdatedEvent,
                         final CloseUnsavedProjectAssetsPopUpPresenter closeUnsavedProjectAssetsPopUpPresenter,
                         final @Source(Source.Kind.EXTERNAL) Event<ImportProjectsSetupEvent> importProjectsSetupEvent,
                         final LibraryBreadcrumbs libraryBreadcrumbs,
                         final SessionInfo sessionInfo,
                         final Caller<RepositoryService> repositoryService,
                         final Promises promises,
                         final OrganizationalUnitController organizationalUnitController,
                         final Caller<OrganizationalUnitService> organizationalUnitService,
                         final Logger logger) {

        this.breadcrumbs = breadcrumbs;
        this.ts = ts;
        this.assetDetailEvent = assetDetailEvent;
        this.libraryService = libraryService;
        this.projectService = projectService;
        this.moduleService = moduleService;
        this.placeManager = placeManager;
        this.projectContext = projectContext;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.notificationEvent = notificationEvent;
        this.translationUtils = translationUtils;
        this.vfsService = vfsService;
        this.importRepositoryPopUpPresenters = importRepositoryPopUpPresenters;
        this.assetListUpdatedEvent = assetListUpdatedEvent;
        this.closeUnsavedProjectAssetsPopUpPresenter = closeUnsavedProjectAssetsPopUpPresenter;
        this.importProjectsSetupEvent = importProjectsSetupEvent;
        this.libraryBreadcrumbs = libraryBreadcrumbs;
        this.sessionInfo = sessionInfo;
        this.repositoryService = repositoryService;
        this.promises = promises;
        this.organizationalUnitController = organizationalUnitController;
        this.organizationalUnitService = organizationalUnitService;
        this.logger = logger;
    }

    @PostConstruct
    public void setup() {
        libraryBreadcrumbs.init(this);

        self = this;
        expose();

        projectContext.addChangeHandler(this);

        placeManager.registerPerspectiveCloseChain(LIBRARY_PERSPECTIVE,
                                                   (chain, place) -> {
                                                       if (LIBRARY_PERSPECTIVE.equals(place.getIdentifier())) {
                                                           closeAllPlacesOrNothing(chain::execute);
                                                       } else {
                                                           closePlace(chain::execute,
                                                                      place);
                                                       }
                                                   });
    }

    private static LibraryPlaces self;

    public static Object nativeGoToSpace(final String spaceName) {
        return self.promises.promisify(self.organizationalUnitService, s -> {
            return s.getOrganizationalUnit(spaceName);
        }).then(space -> {
            self.projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(space));
            return self.goToLibrary();
        });
    }

    public native void expose() /*-{
        $wnd.AppFormer.LibraryPlaces = {
            goToSpace: @org.kie.workbench.common.screens.library.client.util.LibraryPlaces::nativeGoToSpace(Ljava/lang/String;),
            canCreateSpace: @org.kie.workbench.common.screens.library.client.util.LibraryPlaces::nativeUserCanCreateOrganizationalUnit()
        }
    }-*/;


    public static boolean nativeUserCanCreateOrganizationalUnit() {
        return self.userCanCreateOrganizationalUnit();
    }

    public boolean userCanCreateOrganizationalUnit() {
        return this.organizationalUnitController.canCreateOrgUnits();
    }

    public void onSelectPlaceEvent(@Observes final PlaceGainFocusEvent placeGainFocusEvent) {
        if (isLibraryPerspectiveOpen() && !closingLibraryPlaces) {
            final PlaceRequest place = placeGainFocusEvent.getPlace();

            if (place instanceof PathPlaceRequest) {
                libraryBreadcrumbs.setupForAsset(getActiveWorkspace(),
                                                 ((PathPlaceRequest) place).getPath());
            } else if (!place.getIdentifier().equals(ALERTS) && isLibraryPlace(place)) {
                if (projectContext.getActiveWorkspaceProject().isPresent()
                        && place.getIdentifier().equals(LibraryPlaces.PROJECT_SCREEN)) {
                    libraryBreadcrumbs.setupForProject(getActiveWorkspace());
                } else if (projectContext.getActiveOrganizationalUnit().isPresent()
                        && place.getIdentifier().equals(LibraryPlaces.LIBRARY_SCREEN)) {
                    libraryBreadcrumbs.setupForSpace(getActiveSpace());
                }
            }
        }
    }

    /*
     * Re-reroutes this event for project screen. If we tried to observe this directly from the project screen,
     * there are timing issues involved with subscribing to the event.
     */
    public void onAssetListUpdateEvent(@Observes @Remote final ProjectAssetListUpdated event) {
        assetListUpdatedEvent.fire(event);
    }

    private boolean isLibraryPlace(final PlaceRequest place) {
        return LIBRARY_PLACES.contains(place.getIdentifier());
    }

    public void onNewResourceCreated(@Observes final NewResourceSuccessEvent newResourceSuccessEvent) {
        if (isLibraryPerspectiveOpen()) {
            assetDetailEvent.fire(new AssetDetailEvent(projectContext.getActiveWorkspaceProject()
                                                               .orElseThrow(() -> new IllegalStateException("Cannot fire asset detail event without an active project.")),
                                                       newResourceSuccessEvent.getPath()));
            placeManager.closePlace(LibraryPlaces.ADD_ASSET_SCREEN);
        }
    }

    public void onAssetRenamedAccepted(@Observes final ConcurrentRenameAcceptedEvent concurrentRenameAcceptedEvent) {
        if (isLibraryPerspectiveOpen()) {
            final ObservablePath path = concurrentRenameAcceptedEvent.getPath();
            goToAsset(path);
            libraryBreadcrumbs.setupForAsset(getActiveWorkspace(),
                                             path);
        }
    }

    public void onProjectDeleted(@Observes final RepositoryRemovedEvent repositoryRemovedEvent) {
        if (isLibraryPerspectiveOpen() && isRepoForActiveProject(repositoryRemovedEvent)) {
            WorkspaceProjectContextChangeEvent contextChangeEvent = projectContext.getActiveOrganizationalUnit()
                    .map(ou -> new WorkspaceProjectContextChangeEvent(ou))
                    .orElseGet(() -> new WorkspaceProjectContextChangeEvent());
            projectContextChangeEvent.fire(contextChangeEvent);
            closeAllPlaces();
            goToLibrary();
            notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.ProjectDeleted),
                                                         NotificationEvent.NotificationType.DEFAULT));
        }
    }

    public void deleteProject(final WorkspaceProject project,
                              final HasBusyIndicator view) {
        repositoryService.call(v -> view.hideBusyIndicator(),
                               new HasBusyIndicatorDefaultErrorCallback(view)).removeRepository(project.getSpace(),
                                                                                                project.getRepository().getAlias());
    }

    private boolean isRepoForActiveProject(RepositoryRemovedEvent repositoryRemovedEvent) {
        return projectContext.getActiveWorkspaceProject()
                .filter(project -> {
                    Repository activeRepo = project.getRepository();
                    Repository eventRepo = repositoryRemovedEvent.getRepository();
                    return activeRepo.getIdentifier().equals(eventRepo.getIdentifier());
                })
                .isPresent();
    }

    public void onOrganizationalUnitRemoved(@Observes final RemoveOrganizationalUnitEvent removedOrganizationalUnitEvent) {
        final String loggedUser = sessionInfo.getIdentity().getIdentifier();
        if (isLibraryPerspectiveOpen() && !loggedUser.equals(removedOrganizationalUnitEvent.getUserName())) {
            projectContext.getActiveOrganizationalUnit()
                    .filter(active -> active.equals(removedOrganizationalUnitEvent.getOrganizationalUnit()))
                    .ifPresent(active -> this.goToOrganizationalUnits());
        }
    }

    public void onAssetSelected(@Observes final AssetDetailEvent assetDetails) {
        goToAsset(assetDetails.getPath());
    }

    private boolean isLibraryPerspectiveOpen() {
        final PlaceStatus statusPerspective = placeManager.getStatus(LIBRARY_PERSPECTIVE);
        final PlaceStatus statusPerspectiveWithoutRefresh = placeManager.getStatus(getLibraryPlaceRequestWithoutRefresh());
        return statusPerspective.equals(PlaceStatus.OPEN)
                || statusPerspectiveWithoutRefresh.equals(PlaceStatus.OPEN);
    }

    public void onPreferencesSave(@Observes PreferencesCentralSaveEvent event) {
        if (isLibraryPerspectiveOpen()) {
            goToProject();
        }
    }

    public void onPreferencesCancel(@Observes PreferencesCentralUndoChangesEvent event) {
        if (isLibraryPerspectiveOpen()) {
            goToProject();
        }
    }

    PlaceRequest getLibraryPlaceRequestWithoutRefresh() {
        return getPlaceRequestWithoutRefresh(LIBRARY_PERSPECTIVE);
    }

    private PlaceRequest getPlaceRequestWithoutRefresh(String placeId) {
        final Map<String, String> params = new HashMap<>();
        params.put("refresh",
                   "false");
        return new DefaultPlaceRequest(placeId,
                                       params);
    }

    public void refresh(final Command callback) {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        translationUtils.refresh(callback::execute);
    }

    public void goToOrganizationalUnits() {
        closeAllPlacesOrNothing(this::goToSpaces);
    }

    private void goToSpaces() {
        PortablePreconditions.checkNotNull("libraryPerspective.closeAllPlacesOrNothing",
                                           libraryPerspective);
        projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent());

        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.ORGANIZATIONAL_UNITS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
        libraryBreadcrumbs.setupForSpacesScreen();
    }

    public Promise<Void> goToLibrary() {
        if (!projectContext.getActiveOrganizationalUnit().isPresent()) {
            return promises.create((res, rej) -> {
                libraryService.call(
                        (RemoteCallback<OrganizationalUnit>) organizationalUnit -> {
                            this.goToOrganizationalUnits();
                            res.onInvoke((IThenable<Void>) null);
                        },
                        (message, throwable) -> {
                            try {
                                throw throwable;
                            } catch (UnauthorizedException ue) {
                                this.goToOrganizationalUnits();
                                res.onInvoke((IThenable<Void>) null);
                                return false;
                            } catch (Throwable t) {
                                rej.onInvoke(null);
                                return true; // Let default error handling happen.
                            }
                        }).getDefaultOrganizationalUnit();
            });
        } else {
            setupLibraryPerspective();
            return promises.resolve();
        }
    }

    private void setupLibraryPerspective() {
        OrganizationalUnit activeOu = projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot setup library perspective without active space."));
        PortablePreconditions.checkNotNull("libraryPerspective",
                                           libraryPerspective);

        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(activeOu));

        closeLibraryPlaces();
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());

        libraryBreadcrumbs.setupForSpace(activeOu);
    }

    public void goToProject(final WorkspaceProject project) {
        if (projectContext.getActiveWorkspaceProject()
                .map(activeProject -> !activeProject.equals(project))
                .orElse(true)) {
            closeAllPlacesOrNothing(() -> {
                projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(project,
                                                                                      project.getMainModule()));
                goToProject(project,
                            project.getBranch());
            });
        } else {
            goToProject();
        }
    }

    public void goToProject(final WorkspaceProject project,
                            final Branch branch) {
        projectService.call((RemoteCallback<WorkspaceProject>) this::goToProject,
                            (o, throwable) -> {
                                logger.info("Project " + project.getName() + " branch " + branch.getName() + " not found.");
                                return false;
                            }).resolveProject(project.getSpace(), branch);
    }

    void goToProject() {
        goToProject(() -> {
            // do nothing.
        });
    }

    private void goToProject(final Command callback) {
        libraryBreadcrumbs.setupForProject(projectContext.getActiveWorkspaceProject()
                                                   .orElseThrow(() -> new IllegalStateException("Cannot go to project when no project is active.")));

        final PartDefinitionImpl part = new PartDefinitionImpl(new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN));
        part.setSelectable(false);

        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());

        if (callback != null) {
            callback.execute();
        }
    }

    public void goToAsset(final Path path) {

        moduleService.call((RemoteCallback<Package>) response -> {

            projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(projectContext.getActiveWorkspaceProject().orElse(null),
                                                                                  projectContext.getActiveModule().orElse(null),
                                                                                  response));

            final PlaceRequest placeRequest = generatePlaceRequest(path);
            placeManager.goTo(placeRequest);

            if (path != null) {
                final ObservablePath observablePath = ((PathPlaceRequest) placeRequest).getPath();
                observablePath.onRename(() -> libraryBreadcrumbs.setupForAsset(getActiveWorkspace(),
                                                                               observablePath));
            }
        }).resolvePackage(path);
    }

    public void goToAddAsset() {
        final PlaceRequest addAssetScreen = new DefaultPlaceRequest(LibraryPlaces.ADD_ASSET_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(addAssetScreen);
        part.setSelectable(false);
        placeManager.goTo(part,
                          libraryPerspective.getRootPanel());
    }

    public void goToTrySamples() {
        closeAllPlacesOrNothing(() -> {
            final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.IMPORT_SAMPLE_PROJECTS_SCREEN);
            final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
            part.setSelectable(false);

            placeManager.goTo(part,
                              libraryPerspective.getRootPanel());
            libraryBreadcrumbs.setupForTrySamples(getActiveSpace());
        });
    }

    public void goToImportRepositoryPopUp() {
        final ImportRepositoryPopUpPresenter importRepositoryPopUpPresenter = importRepositoryPopUpPresenters.get();
        importRepositoryPopUpPresenter.show();
    }

    public void goToExternalImportPresenter(final Set<ImportProject> projects) {
        closeAllPlacesOrNothing(() -> {
            // TODO add title
            final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.IMPORT_PROJECTS_SCREEN);
            final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
            part.setSelectable(false);

            placeManager.goTo(part,
                              libraryPerspective.getRootPanel());

            setupExternalImportBreadCrumbs();
            importProjectsSetupEvent.fire(new ImportProjectsSetupEvent(projects));
        });
    }

    public void setupExternalImportBreadCrumbs() {
        breadcrumbs.clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  () -> goToOrganizationalUnits());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  projectContext.getActiveOrganizationalUnit()
                                          .orElseThrow(() -> new IllegalStateException("Cannot create library breadcrumb without active space."))
                                          .getName(),
                                  () -> goToLibrary());
        breadcrumbs.addBreadCrumb(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.ImportProjects),
                                  () -> goToImportRepositoryPopUp());
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

    public void closeAllPlacesOrNothing(final Command successCallback) {
        closingLibraryPlaces = true;

        final List<PlaceRequest> uncloseablePlaces = placeManager.getUncloseablePlaces();
        if (uncloseablePlaces != null && uncloseablePlaces.isEmpty()) {
            placeManager.closeAllPlaces();
            closingLibraryPlaces = false;
            if (successCallback != null) {
                successCallback.execute();
            }
        } else {
            final Command newSuccessCallback = () -> {
                placeManager.forceCloseAllPlaces();
                closingLibraryPlaces = false;
                if (successCallback != null) {
                    successCallback.execute();
                }
            };

            closeUnsavedProjectAssetsPopUpPresenter.show(getActiveWorkspace(),
                                                         uncloseablePlaces,
                                                         newSuccessCallback,
                                                         () -> placeManager.goTo(uncloseablePlaces.get(0)));
        }
    }

    public void closePlace(final Command successCallback,
                           final PlaceRequest place) {
        final boolean canClosePlace = placeManager.canClosePlace(place);
        if (canClosePlace) {
            if (successCallback != null) {
                successCallback.execute();
            }
        } else {
            final Command newSuccessCallback = () -> {
                placeManager.forceClosePlace(place);
                if (successCallback != null) {
                    successCallback.execute();
                }
            };

            final List<PlaceRequest> uncloseablePlaces = new ArrayList<>();
            uncloseablePlaces.add(place);
            closeUnsavedProjectAssetsPopUpPresenter.show(getActiveWorkspace(),
                                                         uncloseablePlaces,
                                                         newSuccessCallback,
                                                         () -> {
                                                         });
        }
    }

    void closeAllPlaces() {
        closingLibraryPlaces = true;
        placeManager.closeAllPlaces();
        closingLibraryPlaces = false;
    }

    public WorkspaceProjectContext getWorkbenchContext() {
        return projectContext;
    }

    public WorkspaceProject getActiveWorkspace() {
        return this.projectContext.getActiveWorkspaceProject().orElseThrow(() -> new IllegalStateException("No active workspace project found"));
    }

    public OrganizationalUnit getActiveSpace() {
        return this.projectContext.getActiveOrganizationalUnit().orElseThrow(() -> new IllegalStateException("No active space found"));
    }

    public boolean isThisUserAccessingThisRepository(final User user,
                                                     final Repository repository) {
        return isThisRepositoryBeingAccessed(repository) && sessionInfo.getIdentity().equals(user);
    }

    public boolean isThisRepositoryBeingAccessed(final Repository repository) {
        final Space space = repository.getSpace();
        final String repositoryAlias = repository.getAlias();

        if (this.projectContext.getActiveOrganizationalUnit().isPresent()
                && this.projectContext.getActiveWorkspaceProject().isPresent()) {
            final Space activeSpace = this.projectContext.getActiveOrganizationalUnit().get().getSpace();
            final Repository activeRepository = this.projectContext.getActiveWorkspaceProject().get().getRepository();
            final String activeRepositoryAlias = activeRepository.getAlias();

            return space.equals(activeSpace) && repositoryAlias.equals(activeRepositoryAlias);
        }

        return false;
    }

    public void init(final LibraryPerspective libraryPerspective) {
        this.libraryPerspective = libraryPerspective;
    }

    @Override
    public void onChange(WorkspaceProjectContextChangeEvent previous,
                         WorkspaceProjectContextChangeEvent current) {
        if (current.getWorkspaceProject() != null && !isStandalone()) {
            if (Utils.hasRepositoryChanged(previous.getWorkspaceProject(),
                                           current.getWorkspaceProject())) {
                closeAllPlacesOrNothing(this::goToProject);
            }

            if (Utils.hasModuleChanged(previous.getModule(),
                                       current.getModule())) {
                libraryBreadcrumbs.setupForProject(projectContext.getActiveWorkspaceProject().get());
            }
        }
    }

    public void onDeletedResource(@Observes final ResourceDeletedEvent deleteFileEvent) {
        this.closePathPlace(deleteFileEvent.getPath());
    }

    public void onConcurrentDelete(@Observes final ConcurrentDeleteAcceptedEvent concurrentDeleteAcceptedEvent) {
        this.closePathPlace(concurrentDeleteAcceptedEvent.getPath());
    }

    private void closePathPlace(Path path) {
        this.placeManager.closePlace(new PathPlaceRequest(path));
    }

    private boolean isStandalone() {
        final Map<String, List<String>> parameterMap = getParameterMap();
        if (parameterMap == null) {
            return false;
        } else {
            return parameterMap.containsKey("standalone");
        }
    }

    protected Map<String, List<String>> getParameterMap() {
        return Window.Location.getParameterMap();
    }
}
