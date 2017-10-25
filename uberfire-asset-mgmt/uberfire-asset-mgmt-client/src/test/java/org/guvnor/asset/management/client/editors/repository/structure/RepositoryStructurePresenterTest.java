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

package org.guvnor.asset.management.client.editors.repository.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryStructurePresenterTest {

    @Mock
    private RepositoryStructureView view;

    @Mock
    private ProjectModulesView modulesView;

    @Mock
    private POMService pomService;

    @Mock
    private RepositoryStructureService repositoryStructureService;

    private Event<ProjectContextChangeEvent> contextChangeEvent = new EventSourceMock<ProjectContextChangeEvent>() {
        @Override
        public void fire(ProjectContextChangeEvent event) {
            //Do nothing. Default implementation throws an Exception
        }
    };

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ProjectContext workbenchContext;

    @Mock
    private ProjectWizard wizard;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Mock
    private Repository repository;

    @Mock
    private Project project;

    private RepositoryStructurePresenter presenter;

    @Before
    public void setup() {

        when(project.getIdentifier()).thenReturn("id");

        final Caller<POMService> pomServiceCaller = new CallerMock<POMService>(pomService);
        final Caller<RepositoryStructureService> repositoryStructureServiceCaller = new CallerMock<RepositoryStructureService>(repositoryStructureService);

        when(view.getModulesView()).thenReturn(modulesView);

        presenter = new RepositoryStructurePresenter(view,
                                                     pomServiceCaller,
                                                     repositoryStructureServiceCaller,
                                                     mock(RepositoryStructureTitle.class),
                                                     contextChangeEvent,
                                                     conflictingRepositoriesPopup,
                                                     mock(RepositoryStructureMenu.class),
                                                     placeManager,
                                                     workbenchContext,
                                                     wizard,
                                                     mock(RepositoryManagedStatusUpdater.class)) {
            @Override
            ObservablePath createObservablePath(final Path path) {
                return new ObservablePathImpl().wrap(path);
            }

            @Override
            void destroyObservablePath(final ObservablePath path) {
                //Do nothing
            }
        };
    }

    @Test
    public void testOnStartupNoRepositoryNoProject() throws Exception {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        verify(view,
               times(1)).setModulesViewVisible(eq(false));
        verify(view,
               times(1)).clearDataView();
        verify(modulesView,
               times(1)).enableActions(eq(true));
    }

    @Test
    public void testOnStartupWithRepositoryNoProject() throws Exception {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        verify(view,
               times(1)).setModulesViewVisible(eq(false));
        verify(view,
               times(1)).clearDataView();
        verify(modulesView,
               times(1)).enableActions(eq(true));
    }

    @Test
    public void testOnStartupWithRepositoryWithProjectNoModel() throws Exception {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = null;
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        verify(view,
               times(1)).setModulesViewVisible(eq(false));
        verify(view,
               times(1)).clearDataView();
        verify(modulesView,
               times(1)).enableActions(eq(true));
        verify(view,
               times(1)).showBusyIndicator(Constants.INSTANCE.Loading());
        verify(view,
               times(1)).hideBusyIndicator();
    }

    @Test
    public void testOnStartupWithRepositoryWithProjectWithModel_SingleModule() throws Exception {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        model.setManaged(true);
        model.setOrphanProjects(new ArrayList<Project>() {{
            add(project);
        }});
        model.setOrphanProjectsPOM(new HashMap<String, POM>() {
            {
                put(project.getIdentifier(),
                    new POM(new GAV("groupId",
                                    "artifactId",
                                    "version")));
            }
        });

        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        verify(view,
               times(1)).clearDataView();
        verify(modulesView,
               times(1)).enableActions(eq(true));
        verify(view,
               times(1)).showBusyIndicator(Constants.INSTANCE.Loading());
        verify(view,
               times(1)).hideBusyIndicator();

        verify(view,
               times(1)).setDataPresenterMode(eq(RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT));
        verify(modulesView,
               times(1)).setMode(eq(ProjectModulesView.ViewMode.PROJECTS_VIEW));
        verify(view,
               times(1)).setDataPresenterModel(any(GAV.class));
        verify(view,
               times(1)).setModulesViewVisible(eq(false));
    }

    @Test
    public void testOnStartupWithRepositoryWithProjectWithModel_MultiModule() throws Exception {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setPathToPOM(mock(Path.class));
        model.setManaged(true);
        model.setPOM(pom);
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        verify(view,
               times(1)).clearDataView();
        verify(modulesView,
               times(1)).enableActions(eq(true));
        verify(view,
               times(1)).showBusyIndicator(Constants.INSTANCE.Loading());
        verify(view,
               times(1)).hideBusyIndicator();

        verify(view,
               times(1)).setDataPresenterMode(eq(RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT));
        verify(modulesView,
               times(1)).setMode(eq(ProjectModulesView.ViewMode.MODULES_VIEW));
        verify(view,
               times(1)).setDataPresenterModel(eq(pom.getGav()));
        verify(view,
               times(1)).setModulesViewVisible(eq(true));
    }

    @Test
    public void testOnInitRepositoryStructureNonClashingGAV() {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setManaged(true);
        model.setPOM(pom);
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        presenter.onInitRepositoryStructure();

        verify(view,
               times(2)).showBusyIndicator(eq(Constants.INSTANCE.Loading()));
        verify(view,
               times(1)).showBusyIndicator(eq(Constants.INSTANCE.CreatingRepositoryStructure()));
        verify(view,
               times(3)).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnInitRepositoryStructureClashingGAV() {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setManaged(true);
        model.setPOM(pom);
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException(pom.getGav(),
                                                                            new HashSet<MavenRepositoryMetadata>() {{
                                                                                add(new MavenRepositoryMetadata("local-id",
                                                                                                                "local-url",
                                                                                                                MavenRepositorySource.LOCAL));
                                                                            }});
        doThrow(gae).when(repositoryStructureService).initRepositoryStructure(any(GAV.class),
                                                                              eq(repository),
                                                                              eq(DeploymentMode.VALIDATED));

        presenter.onInitRepositoryStructure();

        verify(repositoryStructureService,
               times(1)).load(eq(repository),
                              anyString());

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(conflictingRepositoriesPopup,
               times(1)).setContent(eq(pom.getGav()),
                                    any(Set.class),
                                    commandArgumentCaptor.capture());
        verify(conflictingRepositoriesPopup,
               times(1)).show();

        verify(view,
               times(1)).showBusyIndicator(eq(Constants.INSTANCE.Loading()));
        verify(view,
               times(1)).showBusyIndicator(eq(Constants.INSTANCE.CreatingRepositoryStructure()));
        verify(view,
               times(1)).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnInitRepositoryStructureClashingGAVForced() {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setManaged(true);
        model.setPOM(pom);
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException(pom.getGav(),
                                                                            new HashSet<MavenRepositoryMetadata>() {{
                                                                                add(new MavenRepositoryMetadata("local-id",
                                                                                                                "local-url",
                                                                                                                MavenRepositorySource.LOCAL));
                                                                            }});
        doThrow(gae).when(repositoryStructureService).initRepositoryStructure(any(GAV.class),
                                                                              eq(repository),
                                                                              eq(DeploymentMode.VALIDATED));

        presenter.onInitRepositoryStructure();

        verify(repositoryStructureService,
               times(1)).load(eq(repository),
                              anyString());

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(conflictingRepositoriesPopup,
               times(1)).setContent(eq(pom.getGav()),
                                    any(Set.class),
                                    commandArgumentCaptor.capture());
        verify(conflictingRepositoriesPopup,
               times(1)).show();

        //Emulate User electing to force save
        assertNotNull(commandArgumentCaptor.getValue());
        commandArgumentCaptor.getValue().execute();

        verify(conflictingRepositoriesPopup,
               times(1)).hide();

        verify(repositoryStructureService,
               times(2)).load(eq(repository),
                              anyString());

        verify(view,
               times(2)).showBusyIndicator(eq(Constants.INSTANCE.Loading()));
        verify(view,
               times(2)).showBusyIndicator(eq(Constants.INSTANCE.CreatingRepositoryStructure()));
        verify(view,
               times(3)).hideBusyIndicator();
    }

    @Test
    public void testOnAddModuleSingleModule() {
        when(organizationalUnit.getDefaultGroupId()).thenReturn("TestUnitByDefGroupId");
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        model.setManaged(true);
        model.setOrphanProjects(new ArrayList<Project>() {{
            add(project);
        }});
        model.setOrphanProjectsPOM(new HashMap<String, POM>() {
            {
                put(project.getIdentifier(),
                    new POM(new GAV("groupId",
                                    "artifactId",
                                    "version")));
            }
        });
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        presenter.onAddModule();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(wizard,
               times(1)).initialise(pomArgumentCaptor.capture());
        verify(wizard,
               times(1)).start(Matchers.<Callback<Project>>any(),
                               eq(false));

        final POM pom = pomArgumentCaptor.getValue();
        assertNotNull(pom);
        assertNotNull(pom.getGav());
        //The organizational unit name should have been suggested as groupId by default.
        assertEquals("TestUnitByDefGroupId",
                     pom.getGav().getGroupId());
        assertNull(pom.getGav().getArtifactId());
        assertNull(pom.getGav().getVersion());
    }

    @Test
    public void testOnAddModuleMultiModule() {
        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(organizationalUnit);
        when(workbenchContext.getActiveRepository()).thenReturn(repository);
        when(workbenchContext.getActiveProject()).thenReturn(project);

        final RepositoryStructureModel model = new RepositoryStructureModel();
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setManaged(true);
        model.setPOM(pom);
        when(repositoryStructureService.load(eq(repository),
                                             anyString())).thenReturn(model);

        when(view.getDataPresenterGav()).thenReturn(new GAV("groupId",
                                                            "artifactId",
                                                            "version"));

        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        presenter.onStartup(placeRequest);

        presenter.onAddModule();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(wizard,
               times(1)).initialise(pomArgumentCaptor.capture());
        verify(wizard,
               times(1)).start(Matchers.<Callback<Project>>any(),
                               eq(false));

        final POM wizardPom = pomArgumentCaptor.getValue();
        assertNotNull(wizardPom);

        final GAV moduleGAV = wizardPom.getGav();
        assertNotNull(moduleGAV);
        assertEquals("groupId",
                     moduleGAV.getGroupId());
        assertNull(moduleGAV.getArtifactId());
        assertEquals("version",
                     moduleGAV.getVersion());

        final GAV parentGav = wizardPom.getParent();
        assertNotNull(parentGav);
        assertEquals("groupId",
                     parentGav.getGroupId());
        assertEquals("artifactId",
                     parentGav.getArtifactId());
        assertEquals("version",
                     parentGav.getVersion());
    }
}
