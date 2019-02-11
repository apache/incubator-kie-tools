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

package org.kie.workbench.common.screens.library.client.screens.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.ProjectScreenTestBase;
import org.kie.workbench.common.screens.library.client.screens.assets.events.UpdatedAssetsEvent;
import org.kie.workbench.common.screens.library.client.util.CategoryUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetItemWidget;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.category.Others;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PopulatedAssetsScreenTest extends ProjectScreenTestBase {

    private PopulatedAssetsScreen populatedAssetsScreen;

    @Mock
    private PopulatedAssetsScreen.View view;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LibraryPlaces libraryPlaces;

    @Mock
    private TranslationService ts;

    @Mock
    private Classifier assetClassifier;

    @Mock
    private ManagedInstance<AssetItemWidget> assetItemWidget;

    @Mock
    private NewFileUploader newFileUploader;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private LibraryService libraryService;

    @Mock
    private LibraryPermissions libraryPermissions;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;

    @Mock
    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private EmptyState emptyState;

    @Mock
    private CategoryUtils categoryUtils;

    @Mock
    private Event<WorkspaceProjectContextChangeEvent> contextChangeEvent;

    private AssetQueryService assetQueryService;

    @Before
    public void setUp() {
        assetQueryService = spy(new AssetQueryService(new CallerMock<>(libraryService)));

        populatedAssetsScreen = spy(new PopulatedAssetsScreen(view,
                                                              categoriesManagerCache,
                                                              resourceTypeManagerCache,
                                                              busyIndicatorView,
                                                              libraryPlaces,
                                                              ts,
                                                              assetClassifier,
                                                              assetItemWidget,
                                                              newFileUploader,
                                                              newResourcePresenter,
                                                              libraryPermissions,
                                                              mock(Event.class),
                                                              emptyState,
                                                              categoryUtils,
                                                              assetQueryService,
                                                              contextChangeEvent));
    }

    @Test
    public void testOffsetGeneration() {

        int offset = this.populatedAssetsScreen.buildOffset(1,
                                                            15);

        assertEquals(0,
                     offset);
    }

    @Test
    public void testTotalPages() {
        {
            int pages = this.populatedAssetsScreen.totalPages(10,
                                                              15);
            assertEquals(1,
                         pages);
        }

        {
            int pages = this.populatedAssetsScreen.totalPages(16,
                                                              15);
            assertEquals(2,
                         pages);
        }
    }

    @Test
    public void testGetAssetsCount() {
        int assetsCount = this.populatedAssetsScreen.getAssetsCount(15,
                                                                    0);

        assertEquals(15,
                     assetsCount);

        assetsCount = this.populatedAssetsScreen.getAssetsCount(15,
                                                                16);

        assertEquals(15,
                     assetsCount);

        assetsCount = this.populatedAssetsScreen.getAssetsCount(15,
                                                                10);

        assertEquals(10,
                     assetsCount);
    }

    @Test
    public void testOnAssetsUpdate() throws Exception {
        final UpdatedAssetsEvent mockedEvent = mock(UpdatedAssetsEvent.class);
        final ProjectAssetsQuery mockedQuery = mock(ProjectAssetsQuery.class);
        doReturn(mockedQuery)
                .when(populatedAssetsScreen)
                .createProjectQuery("",
                                    "ALL",
                                    0,
                                    0);
        populatedAssetsScreen.init();

        populatedAssetsScreen.onAssetsUpdated(mockedEvent);

        verify(assetQueryService).getNumberOfAssets(mockedQuery);
    }

    @Test
    public void updateNoWorkspaceProject() throws Exception {
        doReturn(null).when(libraryPlaces).getActiveWorkspace();

        populatedAssetsScreen.init();
        populatedAssetsScreen.refreshOnFocus(mock(SelectPlaceEvent.class));

        verify(populatedAssetsScreen,
               never()).update();
    }

    @Test
    public void updateNoMainModule() throws Exception {
        doReturn(mock(WorkspaceProject.class)).when(libraryPlaces).getActiveWorkspace();

        populatedAssetsScreen.init();
        populatedAssetsScreen.refreshOnFocus(mock(SelectPlaceEvent.class));

        verify(populatedAssetsScreen,
               never()).update();
    }

    @Test
    public void updateDontHidesBusyIndicator() throws Exception {
        doReturn(mock(WorkspaceProject.class)).when(libraryPlaces).getActiveWorkspace();

        populatedAssetsScreen.init();
        populatedAssetsScreen.refreshOnFocus(mock(SelectPlaceEvent.class));

        populatedAssetsScreen.update();

        verify(busyIndicatorView,
               never()).hideBusyIndicator();
    }

    @Test
    public void searchUpdateCallbackHidesBusyIndicator() throws Exception {

        AssetQueryResult assetQueryResult = AssetQueryResult.normal(new ArrayList<>());
        when(libraryService.getProjectAssets(any())).thenReturn(assetQueryResult);

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();

        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();

        populatedAssetsScreen.init();

        populatedAssetsScreen.search("");

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(populatedAssetsScreen).update(any());
        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void updateNotTheCorrectPlace() throws Exception {
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();

        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();

        populatedAssetsScreen.init();
        final SelectPlaceEvent selectPlaceEvent = mock(SelectPlaceEvent.class);
        doReturn(PlaceRequest.NOWHERE).when(selectPlaceEvent).getPlace();
        populatedAssetsScreen.refreshOnFocus(selectPlaceEvent);

        verify(populatedAssetsScreen,
               never()).update();
    }

    @Test
    public void testNextPage() {
        doNothing().when(this.populatedAssetsScreen).update();
        {
            this.populatedAssetsScreen.setTotalPages(20,
                                                     15);
            this.populatedAssetsScreen.setCurrentPage(1);
            this.populatedAssetsScreen.nextPage();
            assertEquals(2,
                         this.populatedAssetsScreen.getCurrentPage());
        }

        {
            this.populatedAssetsScreen.setTotalPages(1,
                                                     15);
            this.populatedAssetsScreen.setCurrentPage(1);
            this.populatedAssetsScreen.nextPage();
            assertEquals(1,
                         this.populatedAssetsScreen.getCurrentPage());
        }
    }

    @Test
    public void updateDoUpdate() throws Exception {

        doNothing().when(populatedAssetsScreen).update();
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();

        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();

        populatedAssetsScreen.init();
        final SelectPlaceEvent selectPlaceEvent = mock(SelectPlaceEvent.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        doReturn(LibraryPlaces.PROJECT_SCREEN).when(placeRequest).getIdentifier();
        doReturn(placeRequest).when(selectPlaceEvent).getPlace();
        populatedAssetsScreen.refreshOnFocus(selectPlaceEvent);

        verify(populatedAssetsScreen).update();
    }

    @Test
    public void testPrevious() {
        doNothing().when(this.populatedAssetsScreen).update();
        {
            this.populatedAssetsScreen.setTotalPages(20,
                                                     15);
            this.populatedAssetsScreen.setCurrentPage(2);
            this.populatedAssetsScreen.prevPage();
            assertEquals(1,
                         this.populatedAssetsScreen.getCurrentPage());
        }

        {
            this.populatedAssetsScreen.setTotalPages(20,
                                                     15);
            this.populatedAssetsScreen.setCurrentPage(1);
            this.populatedAssetsScreen.prevPage();
            assertEquals(1,
                         this.populatedAssetsScreen.getCurrentPage());
        }
    }

    @Test
    public void testEnableNextPaginationButtons() {
        doNothing().when(this.populatedAssetsScreen).update();
        this.populatedAssetsScreen.setTotalPages(20,
                                                 15);
        {
            this.populatedAssetsScreen.setCurrentPage(1);
            this.populatedAssetsScreen.checkPaginationButtons();
            verify(this.view,
                   times(1)).disablePreviousButton();
            verify(this.view,
                   never()).disableNextButton();
            verify(this.view,
                   times(1)).enableNextButton();
            verify(this.view,
                   never()).enablePreviousButton();
            verify(this.populatedAssetsScreen,
                   times(1)).update();
        }
    }

    @Test
    public void testEnablePreviousPaginationButtons() {
        doNothing().when(this.populatedAssetsScreen).update();
        this.populatedAssetsScreen.setTotalPages(20,
                                                 15);

        {
            this.populatedAssetsScreen.setCurrentPage(2);
            this.populatedAssetsScreen.checkPaginationButtons();
            verify(this.view,
                   times(1)).enablePreviousButton();
            verify(this.view,
                   never()).enableNextButton();
            verify(this.view,
                   times(1)).disableNextButton();
            verify(this.view,
                   never()).disablePreviousButton();
            verify(this.populatedAssetsScreen,
                   times(1)).update();
        }
    }

    @Test
    public void testSetCurrentPage() {
        doNothing().when(this.populatedAssetsScreen).update();

        this.populatedAssetsScreen.setTotalPages(20,
                                                 15);

        this.populatedAssetsScreen.setCurrentPage(1);
        this.populatedAssetsScreen.setCurrentPage(10);

        verify(this.populatedAssetsScreen,
               times(1)).update();

        verify(this.view,
               times(1)).setCurrentPage(anyInt());
    }

    @Test
    public void testCreateProjectQuery() {

        List<String> more = Arrays.asList("xml",
                                          "java",
                                          "dsl");

        String filter = "filter";
        Others other = new Others();
        doReturn(other).when(this.categoriesManagerCache).getCategory(eq(new Others().getName()));
        doReturn(more).when(this.populatedAssetsScreen).getSuffixes(eq(other));

        {
            String filterType = "ALL";
            ProjectAssetsQuery query = this.populatedAssetsScreen.createProjectQuery(filter,
                                                                                     filterType,
                                                                                     0,
                                                                                     10);

            assertEquals("filter",
                         query.getFilter());
            assertThat(query.getExtensions(),
                       is(Collections.emptyList()));
        }

        {
            String filterType = new Others().getName();
            ProjectAssetsQuery query = this.populatedAssetsScreen.createProjectQuery(filter,
                                                                                     filterType,
                                                                                     0,
                                                                                     10);

            assertEquals("filter",
                         query.getFilter());
            assertThat(query.getExtensions(),
                       is(more));
        }
    }

    @Test
    public void selectCommandTest() {
        final Path assetPath = mock(Path.class);

        this.populatedAssetsScreen.selectCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(assetPath);
    }

    @Test
    public void detailsCommandTest() {
        final Path assetPath = mock(Path.class);

        this.populatedAssetsScreen.detailsCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(assetPath);
    }
}