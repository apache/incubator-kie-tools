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

package org.kie.workbench.common.screens.library.client.screens.samples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ExamplesImportPresenter;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.example.ExampleProjectWidget;
import org.kie.workbench.common.screens.library.client.widgets.example.branchselector.BranchSelectorPopUpPresenter;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectErrorPresenter;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectOkPresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExampleImportPresenterTest {

    @Mock
    private ImportPresenter.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private ManagedInstance<ExampleProjectWidget> tileWidgets;

    @Mock
    private ExamplesService examplesService;
    private Caller<ExamplesService> examplesServiceCaller;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private EventSourceMock<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExampleProjectOkPresenter exampleProjectOkPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExampleProjectErrorPresenter exampleProjectErrorPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BranchSelectorPopUpPresenter branchSelectorPopUpPresenter;

    private ExampleProjectWidget tileWidget;

    private ImportPresenter importPresenter;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        examplesServiceCaller = new CallerMock<>(examplesService);
        tileWidget = spy(new ExampleProjectWidget(mock(ExampleProjectWidget.View.class),
                                                  exampleProjectOkPresenter,
                                                  exampleProjectErrorPresenter,
                                                  branchSelectorPopUpPresenter));

        doReturn(tileWidget).when(tileWidgets).get();

        importPresenter = new ExamplesImportPresenter(view,
                                                      libraryPlaces,
                                                      tileWidgets,
                                                      examplesServiceCaller,
                                                      mock(WorkspaceProjectContext.class),
                                                      notificationEvent,
                                                      projectContextChangeEvent,
                                                      new Elemental2DomUtil(),
                                                      mock(TranslationService.class));
    }

    @Test
    public void onStartupWithoutProjectsTest() {
        Map<String, String> params = new HashMap<>();
        params.put("repositoryUrl",
                   "repoUrl");
        importPresenter.onStartup(new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN,
                                                          params));

        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
        verify(libraryPlaces).goToLibrary();
    }

    @Test
    public void filterProjectsTest() {
        Set<ImportProject> projects = new HashSet<>();
        projects.add(new ImportProject(mock(Path.class),
                                       "p1a",
                                       "p1a description",
                                       "git@git.com",
                                       null));
        projects.add(new ImportProject(mock(Path.class),
                                       "p3b",
                                       "p3b description",
                                       "git@git.com",
                                       null));
        projects.add(new ImportProject(mock(Path.class),
                                       "p2a",
                                       "p2a description",
                                       "git@git.com",
                                       null));
        doReturn(projects).when(examplesService).getExampleProjects();

        Map<String, String> params = new HashMap<>();
        params.put("repositoryUrl",
                   "repoUrl");
        importPresenter.onStartup(new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN,
                                                          params));

        final List<ExampleProjectWidget> filteredProjects = importPresenter.filterProjects("a");

        assertEquals(2,
                     filteredProjects.size());
    }

    @Test
    public void cancelTest() {
        importPresenter.cancel();

        verify(libraryPlaces).goToLibrary();
    }
}
