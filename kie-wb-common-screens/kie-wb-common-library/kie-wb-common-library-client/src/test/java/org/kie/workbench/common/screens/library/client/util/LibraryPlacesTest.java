/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.event.Event;

import org.ext.uberfire.social.activities.model.ExtendedTypes;
import org.ext.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.RenameModuleEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.project.social.ModuleEventType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.WorkbenchProjectMetricsEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportProjectsSetupEvent;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.close.CloseUnsavedProjectAssetsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.widgets.library.LibraryToolbarPresenter;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibraryPlacesTest {

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private TranslationService ts;

    @Mock
    private Event<WorkbenchProjectMetricsEvent> projectMetricsEvent;

    @Mock
    private Event<AssetDetailEvent> assetDetailEvent;

    @Mock
    private ResourceUtils resourceUtils;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private LibraryToolbarPresenter libraryToolbar;

    @Mock
    private AuthoringWorkbenchDocks docks;

    @Mock
    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private TranslationUtils translationUtils;

    @Mock
    private VFSService vfsService;
    private Caller<VFSService> vfsServiceCaller;

    @Mock
    private ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    @Mock
    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    @Mock
    private ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters;

    @Mock
    private ImportRepositoryPopUpPresenter importRepositoryPopUpPresenter;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private WorkspaceProjectContextChangeEvent previous;

    @Mock
    private WorkspaceProjectContextChangeEvent current;

    @Mock
    private Event<ProjectAssetListUpdated> assetListUpdateEvent;

    @Mock
    private CloseUnsavedProjectAssetsPopUpPresenter closeUnsavedProjectAssetsPopUpPresenter;

    @Mock
    private Event<ImportProjectsSetupEvent> projectsSetupEvent;

    @Captor
    private ArgumentCaptor<WorkspaceProjectContextChangeEvent> projectContextChangeEventArgumentCaptor;

    private LibraryPlaces libraryPlaces;

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository activeRepository;
    private Branch activeBranch;
    private Module activeModule;
    private WorkspaceProject activeProject;
    private HashMap<String, List<String>> windowParameters;

    @Before
    public void setup() {
        windowParameters = new HashMap<>();
        libraryServiceCaller = new CallerMock<>(libraryService);
        vfsServiceCaller = new CallerMock<>(vfsService);

        final UberElement libraryToolBarView = mock(UberElement.class);
        doReturn(libraryToolBarView).when(libraryToolbar).getView();

        libraryPlaces = spy(new LibraryPlaces(breadcrumbs,
                                              ts,
                                              projectMetricsEvent,
                                              assetDetailEvent,
                                              resourceUtils,
                                              libraryServiceCaller,
                                              new CallerMock<>(projectService),
                                              new CallerMock<>(moduleService),
                                              placeManager,
                                              projectContext,
                                              libraryToolbar,
                                              docks,
                                              projectContextChangeEvent,
                                              notificationEvent,
                                              translationUtils,
                                              vfsServiceCaller,
                                              projectScopedResolutionStrategySupplier,
                                              preferencesCentralInitializationEvent,
                                              importRepositoryPopUpPresenters,
                                              assetListUpdateEvent,
                                              closeUnsavedProjectAssetsPopUpPresenter,
                                              projectsSetupEvent){

            @Override
            protected Map<String, List<String>> getParameterMap() {
                return windowParameters;
            }
        });
        libraryPlaces.setup();

        verify(libraryToolBarView).getElement();

        libraryPlaces.init(mock(LibraryPerspective.class));

        activeOrganizationalUnit = mock(OrganizationalUnit.class);
        activeRepository = mock(Repository.class);
        activeBranch = new Branch("master",
                                  mock(Path.class));
        activeModule = mock(Module.class);

        doReturn(Optional.of(activeOrganizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        activeProject = new WorkspaceProject(activeOrganizationalUnit,
                                             activeRepository,
                                             activeBranch,
                                             activeModule);
        doReturn(Optional.of(activeProject)).when(projectContext).getActiveWorkspaceProject();
        doReturn(Optional.of(activeModule)).when(projectContext).getActiveModule();
        doReturn(Optional.empty()).when(projectContext).getActiveRepositoryRoot();
        doReturn(Optional.empty()).when(projectContext).getActivePackage();

        when(current.getOrganizationalUnit()).thenReturn(activeOrganizationalUnit);
        when(current.getWorkspaceProject()).thenReturn(activeProject);
        when(current.getModule()).thenReturn(activeModule);

        final URIStructureExplorerModel model = mock(URIStructureExplorerModel.class);
        doReturn(mock(Repository.class)).when(model).getRepository();
        doReturn(mock(Module.class)).when(model).getModule();

        doReturn(mock(Path.class)).when(vfsService).get(any());

        doNothing().when(libraryPlaces).setupLibraryBreadCrumbs();
        doNothing().when(libraryPlaces).setupLibraryBreadCrumbsForAsset(any(Path.class));
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(mock(ObservablePath.class)).when(pathPlaceRequest).getPath();
        doReturn(pathPlaceRequest).when(libraryPlaces).createPathPlaceRequest(any());

        doReturn(importRepositoryPopUpPresenter).when(importRepositoryPopUpPresenters).get();
    }

    @Test
    public void projectContextListenerIsSetup() {
        verify(projectContext).addChangeHandler(any(LibraryPlaces.class));
    }

    @Test
    public void onChange() {

        when(current.getModule()).thenReturn(null);
        libraryPlaces.onChange(previous,
                               current);

        verify(libraryPlaces).goToProject();
    }

    @Test
    public void onChangeWithActiveModule() {

        when(current.getModule()).thenReturn(mock(Module.class));
        libraryPlaces.onChange(previous,
                               current);

        verify(libraryPlaces).goToProject();
    }

    @Test
    public void onChangeNoActiveProject() {

        doReturn(Optional.empty()).when(projectContext).getActiveWorkspaceProject();
        when(current.getModule()).thenReturn(mock(Module.class));
        when(current.getWorkspaceProject()).thenReturn(null);

        libraryPlaces.onChange(previous,
                               current);

        verify(libraryPlaces,
               never()).goToProject();
    }

    @Test
    public void onChangeSameProject() {
        when(previous.getWorkspaceProject()).thenReturn(activeProject);
        when(current.getWorkspaceProject()).thenReturn(activeProject);

        libraryPlaces.onChange(previous,
                               current);

        verify(libraryPlaces,
               never()).goToProject();
        verify(libraryPlaces,
               never()).closeAllPlacesOrNothing(any());
    }

    @Test
    public void onChangeStandaloneActive() {
        windowParameters.put("standalone", null);
        when(current.getModule()).thenReturn(null);
        libraryPlaces.onChange(previous,
                               current);

        verify(libraryPlaces, never()).goToProject();
    }

    @Test
    public void onSelectPlaceOutsideLibraryTest() {
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(any(PlaceRequest.class));

        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(placeGainFocusEvent,
               never()).getPlace();
    }

    @Test
    public void onSelectAssetTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(path).when(pathPlaceRequest).getPath();
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(pathPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).setupLibraryBreadCrumbsForAsset(path);
        verify(libraryPlaces).showDocks();
    }

    @Test
    public void onSelectProjectSettingsTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final DefaultPlaceRequest projectSettingsPlaceRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SETTINGS);
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(projectSettingsPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).hideDocks();
        verify(libraryPlaces).setupLibraryBreadCrumbsForAsset(null);
    }

    @Test
    public void onSelectProjectTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final DefaultPlaceRequest projectSettingsPlaceRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(projectSettingsPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).hideDocks();
        verify(libraryPlaces).setupLibraryBreadCrumbs();
    }

    @Test
    public void onSelectLibraryTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final DefaultPlaceRequest projectSettingsPlaceRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(projectSettingsPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).hideDocks();
        verify(libraryPlaces).setupLibraryBreadCrumbsWithoutProject();
    }

    @Test
    public void hideDocksTest() {
        libraryPlaces.showDocks();

        reset(docks);

        libraryPlaces.hideDocks();
        libraryPlaces.hideDocks();

        verify(docks,
               times(1)).hide();
        verify(docks,
               never()).setup(anyString(),
                              any(PlaceRequest.class));
        verify(docks,
               never()).show();
        verify(docks,
               never()).expandProjectExplorer();
    }

    @Test
    public void showDocksTest() {
        libraryPlaces.showDocks();
        libraryPlaces.showDocks();

        verify(docks,
               times(1)).setup(LibraryPlaces.LIBRARY_PERSPECTIVE,
                               new DefaultPlaceRequest(LibraryPlaces.PROJECT_EXPLORER));
        verify(docks,
               times(1)).show();
        verify(docks,
               never()).hide();
    }

    @Test
    public void onPreferencesSaveTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doNothing().when(libraryPlaces).goToProject();

        libraryPlaces.onPreferencesSave(mock(PreferencesCentralSaveEvent.class));

        verify(libraryPlaces).goToProject();
    }

    @Test
    public void onPreferencesSaveOutsideLibraryTest() {
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(any(PlaceRequest.class));

        libraryPlaces.onPreferencesSave(mock(PreferencesCentralSaveEvent.class));

        verify(libraryPlaces,
               never()).goToProject();
    }

    @Test
    public void onPreferencesCancelTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doNothing().when(libraryPlaces).goToProject();

        libraryPlaces.onPreferencesCancel(mock(PreferencesCentralUndoChangesEvent.class));

        verify(libraryPlaces).goToProject();
    }

    @Test
    public void onPreferencesCancelOutsideLibraryTest() {
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(any(PlaceRequest.class));

        libraryPlaces.onPreferencesCancel(mock(PreferencesCentralUndoChangesEvent.class));

        verify(libraryPlaces,
               never()).goToProject();
    }

    @Test
    public void goToOrganizationalUnitsTest() {
        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.ORGANIZATIONAL_UNITS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToOrganizationalUnits();

        verify(projectContextChangeEvent).fire(projectContextChangeEventArgumentCaptor.capture());
        assertNull(projectContextChangeEventArgumentCaptor.getValue().getOrganizationalUnit());

        final ArgumentCaptor<WorkspaceProjectContextChangeEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WorkspaceProjectContextChangeEvent.class);
        verify(projectContextChangeEvent).fire(eventArgumentCaptor.capture());
        final WorkspaceProjectContextChangeEvent event = eventArgumentCaptor.getValue();
        assertNull(event.getOrganizationalUnit());
        assertNull(event.getWorkspaceProject());
        verify(placeManager).closeAllPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbs();
    }

    @Test
    public void goToAssetTest() {
        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(path).when(pathPlaceRequest).getPath();
        doReturn(pathPlaceRequest).when(libraryPlaces).createPathPlaceRequest(any(Path.class));

        libraryPlaces.goToAsset(path);

        verify(placeManager).goTo(pathPlaceRequest);
        final ArgumentCaptor<WorkspaceProjectContextChangeEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WorkspaceProjectContextChangeEvent.class);
        verify(projectContextChangeEvent).fire(eventArgumentCaptor.capture());

        final WorkspaceProjectContextChangeEvent value = eventArgumentCaptor.getValue();
        assertEquals(activeProject,
                     value.getWorkspaceProject());
        assertEquals(activeModule,
                     value.getModule());
        assertNull(value.getPackage());
    }

    @Test
    public void goToAssetTestWithPackage() {

        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(path).when(pathPlaceRequest).getPath();
        doReturn(pathPlaceRequest).when(libraryPlaces).createPathPlaceRequest(any(Path.class));

        final Package pkg = mock(Package.class);
        doReturn(pkg).when(moduleService).resolvePackage(path);

        libraryPlaces.goToAsset(path);

        verify(projectContextChangeEvent).fire(projectContextChangeEventArgumentCaptor.capture());
        final WorkspaceProjectContextChangeEvent contextChangeEvent = projectContextChangeEventArgumentCaptor.getValue();
        assertEquals(activeProject,
                     contextChangeEvent.getWorkspaceProject());
        assertEquals(activeModule,
                     contextChangeEvent.getModule());
        assertEquals(pkg,
                     contextChangeEvent.getPackage());

        verify(placeManager).goTo(pathPlaceRequest);
        final ArgumentCaptor<WorkspaceProjectContextChangeEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WorkspaceProjectContextChangeEvent.class);
        verify(projectContextChangeEvent).fire(eventArgumentCaptor.capture());

        final WorkspaceProjectContextChangeEvent value = eventArgumentCaptor.getValue();
        assertEquals(activeProject,
                     value.getWorkspaceProject());
        assertEquals(activeModule,
                     value.getModule());
        assertEquals(pkg,
                     value.getPackage());
    }

    @Test
    public void goToProjectSettingsTest() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SETTINGS);

        libraryPlaces.goToAsset(null);

        verify(placeManager).goTo(placeRequest);
    }

    @Test
    public void goToLibraryWithDefaultOrganizationalUnitTest() {
        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty())
                .thenReturn(Optional.of(mock(OrganizationalUnit.class)));
        doReturn(Optional.empty()).when(projectContext).getActiveWorkspaceProject();
        doReturn(Optional.empty()).when(projectContext).getActiveModule();

        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToLibrary();

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsWithoutProject();
        verify(projectContextChangeEvent,
               times(2)).fire(any(WorkspaceProjectContextChangeEvent.class));
    }

    @Test
    public void goToLibraryFromOrganizationalUnitsScreenTest() {
        doReturn(Optional.of(activeOrganizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        doReturn(Optional.empty()).when(projectContext).getActiveWorkspaceProject();
        doReturn(Optional.empty()).when(projectContext).getActiveModule();

        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToLibrary();

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsWithoutProject();
        verify(projectContextChangeEvent,
               times(1)).fire(any(WorkspaceProjectContextChangeEvent.class));
    }

    @Test
    public void goToLibraryWhenGoingBackFromProjectTest() {
        doReturn(Optional.of(activeOrganizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        activeProject = new WorkspaceProject(activeOrganizationalUnit,
                                             activeRepository,
                                             activeBranch,
                                             activeModule);
        doReturn(Optional.of(activeProject)).when(projectContext).getActiveWorkspaceProject();
        doReturn(Optional.of(activeModule)).when(projectContext).getActiveModule();

        final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.LIBRARY_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToLibrary();

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsWithoutProject();
        verify(projectContextChangeEvent,
               never()).fire(any(WorkspaceProjectContextChangeEvent.class));
    }

    @Test
    public void goToLibraryWhenUserIsNotAuthorizedToCreateProjectsTest() {
        doReturn(Optional.ofNullable(null)).when(projectContext).getActiveOrganizationalUnit();
        when(libraryService.getDefaultOrganizationalUnit()).thenThrow(new UnauthorizedException());
        libraryPlaces.goToLibrary();
        verify(libraryPlaces).goToOrganizationalUnits();
    }

    @Test
    public void goToProjectTest() {
        final PlaceRequest projectScreen = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(projectScreen);
        part.setSelectable(false);

        libraryPlaces.goToProject();

        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(projectContextChangeEvent,
               never()).fire(any(WorkspaceProjectContextChangeEvent.class));
        verify(libraryPlaces).setupLibraryBreadCrumbs(activeProject);
    }

    @Test
    public void goToOrgUnitsMetricsTest() {
        final PlaceRequest metricsScreen = new DefaultPlaceRequest(LibraryPlaces.ORG_UNITS_METRICS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(metricsScreen);
        part.setSelectable(false);

        libraryPlaces.goToOrgUnitsMetrics();

        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsForOrgUnitsMetrics();
    }

    @Test
    public void goToProjectMetricsTest() {
        final PlaceRequest projectScreen = new DefaultPlaceRequest(LibraryPlaces.PROJECT_METRICS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(projectScreen);
        part.setSelectable(false);

        libraryPlaces.goToProjectMetrics();

        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(projectMetricsEvent).fire(any(WorkbenchProjectMetricsEvent.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsForProjectMetrics();
    }

    @Test
    public void goToTrySamplesTest() {
        final PlaceRequest trySamplesScreen = new DefaultPlaceRequest(LibraryPlaces.IMPORT_SAMPLE_PROJECTS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(trySamplesScreen);
        part.setSelectable(false);

        libraryPlaces.goToTrySamples();

        verify(libraryPlaces).closeAllPlacesOrNothing(any());
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsForTrySamples();
    }

    @Test
    public void goToExternalImportProjectsTest() {
        doAnswer(inv -> {
            final Command command = inv.getArgumentAt(0,
                                                      Command.class);
            command.execute();
            return null;
        }).when(libraryPlaces).closeAllPlacesOrNothing(any());

        final PlaceRequest trySamplesScreen = new DefaultPlaceRequest(LibraryPlaces.IMPORT_PROJECTS_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(trySamplesScreen);
        part.setSelectable(false);

        final Set<ExampleProject> projects = singleton(new ExampleProject(PathFactory.newPath("example",
                                                                                              "default://master@system/repo/example"),
                                                                          "example",
                                                                          "description",
                                                                          emptyList()));
        libraryPlaces.goToExternalImportPresenter(projects);

        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupExternalImportBreadCrumbs();
    }

    @Test
    public void goToImportRepositoryPopUpTest() {
        libraryPlaces.goToImportRepositoryPopUp();
        verify(importRepositoryPopUpPresenter).show();
    }

    @Test
    public void closeLibraryPlacesTest() {
        libraryPlaces.closeLibraryPlaces();
        verify(placeManager).closePlace(LibraryPlaces.LIBRARY_SCREEN);
        verify(placeManager).closePlace(LibraryPlaces.PROJECT_SCREEN);
        verify(placeManager).closePlace(LibraryPlaces.PROJECT_METRICS_SCREEN);
        verify(placeManager).closePlace(LibraryPlaces.PROJECT_DETAIL_SCREEN);
        verify(placeManager).closePlace(LibraryPlaces.ORGANIZATIONAL_UNITS_SCREEN);
        verify(placeManager).closePlace(LibraryPlaces.PROJECT_SETTINGS);
        verify(placeManager).closePlace(PreferencesRootScreen.IDENTIFIER);
    }

    @Test
    public void goToNewProject() {

        final WorkspaceProject project = new WorkspaceProject(activeOrganizationalUnit,
                                                              activeRepository,
                                                              activeBranch,
                                                              mock(Module.class));
        libraryPlaces.goToProject(project);

        verify(projectContextChangeEvent).fire(any(WorkspaceProjectContextChangeEvent.class));
        verify(placeManager).closeAllPlaces();
    }

    @Test
    public void goToSameProjectTest() {

        final WorkspaceProject project = new WorkspaceProject(activeOrganizationalUnit,
                                                              activeRepository,
                                                              activeBranch,
                                                              activeModule);
        libraryPlaces.goToProject(project);

        verify(projectContextChangeEvent,
               never()).fire(any(WorkspaceProjectContextChangeEvent.class));
        verify(placeManager,
               never()).forceCloseAllPlaces();
    }

    @Test
    public void placesAreUpdatedWhenActiveModuleIsRenamedTest() {
        final Module renamedModule = mock(Module.class);
        final RenameModuleEvent renameModuleEvent = mock(RenameModuleEvent.class);

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        doReturn(activeModule).when(renameModuleEvent).getOldModule();
        doReturn(renamedModule).when(renameModuleEvent).getNewModule();

        libraryPlaces.onProjectRenamed(renameModuleEvent);

        verify(breadcrumbs).clearBreadcrumbs(LibraryPlaces.LIBRARY_PERSPECTIVE);
    }

    @Test
    public void breadcrumbIsNotUpdatedWhenInactiveModuleIsRenamedTest() {
        final Module activeModule = mock(Module.class);
        final Module renamedModule = mock(Module.class);
        final Module otherModule = mock(Module.class);
        final RenameModuleEvent renameModuleEvent = mock(RenameModuleEvent.class);

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        doReturn(Optional.of(activeModule)).when(projectContext).getActiveModule();
        doReturn(otherModule).when(renameModuleEvent).getOldModule();
        doReturn(renamedModule).when(renameModuleEvent).getNewModule();

        libraryPlaces.onProjectRenamed(renameModuleEvent);

        verify(libraryPlaces,
               never()).setupLibraryBreadCrumbsForAsset(any());
    }

    @Test
    public void testOnSocialFileSelected_Repository() {

        doReturn(mock(WorkspaceProject.class)).when(projectService).resolveProject(any(Path.class));

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final SocialFileSelectedEvent event = new SocialFileSelectedEvent(ExtendedTypes.NEW_REPOSITORY_EVENT.name(),
                                                                          null);

        libraryPlaces.onSocialFileSelected(event);

        verify(placeManager).goTo(LibraryPlaces.REPOSITORY_STRUCTURE_SCREEN);
    }

    @Test
    public void testOnSocialFileSelected_Module() {

        doReturn(mock(WorkspaceProject.class)).when(projectService).resolveProject(any(Path.class));

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final PlaceRequest libraryPerspective = libraryPlaces.getLibraryPlaceRequestWithoutRefresh();
        final SocialFileSelectedEvent event = new SocialFileSelectedEvent(ModuleEventType.NEW_MODULE.name(),
                                                                          null);

        libraryPlaces.onSocialFileSelected(event);

        verify(placeManager).goTo(libraryPerspective);
        verify(libraryPlaces).goToProject();
    }

    @Test
    public void testOnSocialFileSelected_Asset() {

        doReturn(mock(WorkspaceProject.class)).when(projectService).resolveProject(any(Path.class));

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final PlaceRequest libraryPerspective = libraryPlaces.getLibraryPlaceRequestWithoutRefresh();
        final SocialFileSelectedEvent event = new SocialFileSelectedEvent("any",
                                                                          "uri");

        libraryPlaces.onSocialFileSelected(event);

        verify(placeManager).goTo(libraryPerspective);
        verify(libraryPlaces).goToAsset(any(Path.class));
    }

    @Test
    public void closeAllPlacesOrNothingWithUncloseablePlacesTest() {
        final Command successCallback = mock(Command.class);

        final List<PlaceRequest> uncloseablePlaces = new ArrayList<>();
        uncloseablePlaces.add(mock(PlaceRequest.class));
        doReturn(uncloseablePlaces).when(placeManager).getUncloseablePlaces();

        libraryPlaces.closeAllPlacesOrNothing(successCallback);

        verify(placeManager,
               never()).forceCloseAllPlaces();
        verify(successCallback,
               never()).execute();
        verify(closeUnsavedProjectAssetsPopUpPresenter).show(eq(activeProject),
                                                             eq(uncloseablePlaces),
                                                             any(),
                                                             any());
    }

    @Test
    public void closeAllPlacesOrNothingWithoutUncloseablePlacesTest() {
        final Command successCallback = mock(Command.class);

        final List<PlaceRequest> uncloseablePlaces = new ArrayList<>();
        uncloseablePlaces.add(mock(PlaceRequest.class));

        libraryPlaces.closeAllPlacesOrNothing(successCallback);

        verify(placeManager).closeAllPlaces();
        verify(successCallback).execute();
        verify(closeUnsavedProjectAssetsPopUpPresenter,
               never()).show(any(),
                             any(),
                             any(),
                             any());
    }

    @Test
    public void testOnResourceCopiedEvent() {

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        Path path = mock(Path.class);
        ResourceCopiedEvent event = mock(ResourceCopiedEvent.class);
        when(event.getDestinationPath()).thenReturn(path);

        this.libraryPlaces.onResourceCopiedEvent(event);

        verify(this.libraryPlaces).goToAsset(path);
    }
}
