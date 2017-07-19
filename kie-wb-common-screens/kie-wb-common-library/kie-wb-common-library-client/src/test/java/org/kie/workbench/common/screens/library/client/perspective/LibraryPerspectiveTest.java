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
package org.kie.workbench.common.screens.library.client.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LibraryPerspectiveTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ContextualSearch contextualSearch;

    @Mock
    private EventSourceMock<FilterUpdateEvent> filterUpdateEvent;

    @Mock
    private PlaceManager placeManager;

    private LibraryPerspective perspective;

    @Before
    public void setup() {
        perspective = new LibraryPerspective(libraryPlaces,
                                             contextualSearch,
                                             filterUpdateEvent,
                                             placeManager);
    }

    @Test
    public void libraryRefreshesPlacesOnStartupTest() {
        perspective.onOpen();

        verify(libraryPlaces).refresh(any());
    }

    @Test
    public void libraryRegisterSearchHandlerTest() {
        perspective.registerSearchHandler();

        verify(contextualSearch).setPerspectiveSearchBehavior(eq(LibraryPlaces.LIBRARY_PERSPECTIVE),
                                                              any());
    }

    @Test
    public void searchBehaviorWhenProjectScreenIsOpenedTest() {
        doReturn(new ProjectInfo(mock(OrganizationalUnit.class),
                                 mock(Repository.class),
                                 "master",
                                 mock(Project.class))).when(libraryPlaces).getProjectInfo();
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.PROJECT_SCREEN);

        perspective.getSearchBehavior().execute("asset");

        verify(libraryPlaces,
               never()).goToProject(any(),
                                    anyBoolean(),
                                    any());
        verify(placeManager).goTo(LibraryPlaces.PROJECT_SCREEN);
        verify(filterUpdateEvent).fire(any());
    }

    @Test
    public void searchBehaviorWhenProjectScreenIsClosedTest() {
        doReturn(new ProjectInfo(mock(OrganizationalUnit.class),
                                 mock(Repository.class),
                                 "master",
                                 mock(Project.class))).when(libraryPlaces).getProjectInfo();
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(LibraryPlaces.PROJECT_SCREEN);

        perspective.getSearchBehavior().execute("asset");

        verify(libraryPlaces).goToProject(any(),
                                          anyBoolean(),
                                          any());
        verify(placeManager,
               never()).goTo(LibraryPlaces.PROJECT_SCREEN);
        verify(filterUpdateEvent,
               never()).fire(any());
    }

    @Test
    public void searchBehaviorWhenNoProjectIsOpenedTest() {
        doReturn(new ProjectInfo(mock(OrganizationalUnit.class),
                                 mock(Repository.class),
                                 "master",
                                 null)).when(libraryPlaces).getProjectInfo();

        perspective.getSearchBehavior().execute("asset");

        verify(libraryPlaces,
               never()).goToProject(any(),
                                    anyBoolean(),
                                    any());
        verify(placeManager,
               never()).goTo(LibraryPlaces.PROJECT_SCREEN);
        verify(filterUpdateEvent,
               never()).fire(any());
    }

    @Test
    public void libraryHidesDocksOnCloseTest() {
        perspective.onClose();

        verify(libraryPlaces).hideDocks();
    }
}