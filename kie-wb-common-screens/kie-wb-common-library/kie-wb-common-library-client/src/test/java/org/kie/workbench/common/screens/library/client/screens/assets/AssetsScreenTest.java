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
 *
 */

package org.kie.workbench.common.screens.library.client.screens.assets;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetsScreenTest {

    private AssetsScreen assetsScreen;

    @Mock
    private AssetsScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EmptyAssetsScreen emptyAssetsScreen;

    @Mock
    private PopulatedAssetsScreen populatedAssetsScreen;

    @Mock
    private InvalidProjectScreen invalidProjectScreen;

    @Mock
    private TranslationService ts;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private LibraryService libraryService;
    private WorkspaceProject workspaceProject;

    @Before
    public void setUp() {

        workspaceProject = mock(WorkspaceProject.class);
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        when(libraryPlaces.getActiveWorkspace()).thenReturn(workspaceProject);

        EmptyAssetsView emptyView = mock(EmptyAssetsView.class);
        PopulatedAssetsView populatedView = mock(PopulatedAssetsView.class);
        InvalidProjectView invalidProjectView = mock(InvalidProjectView.class);

        HTMLElement emptyElement = mock(HTMLElement.class);
        HTMLElement populatedElement = mock(HTMLElement.class);
        HTMLElement invalidProjectElement = mock(HTMLElement.class);

        when(emptyAssetsScreen.getView()).thenReturn(emptyView);
        when(emptyView.getElement()).thenReturn(emptyElement);

        when(populatedAssetsScreen.getView()).thenReturn(populatedView);
        when(populatedView.getElement()).thenReturn(populatedElement);

        when(invalidProjectScreen.getView()).thenReturn(invalidProjectView);
        when(invalidProjectView.getElement()).thenReturn(invalidProjectElement);

        this.assetsScreen = spy(new AssetsScreen(view,
                                                 libraryPlaces,
                                                 emptyAssetsScreen,
                                                 populatedAssetsScreen,
                                                 invalidProjectScreen,
                                                 ts,
                                                 busyIndicatorView,
                                                 new CallerMock<>(libraryService)));
    }

    @Test
    public void testShowEmptyScreenAssets() {
        when(libraryService.hasAssets(any(WorkspaceProject.class))).thenReturn(false);
        this.assetsScreen.init();
        verify(emptyAssetsScreen,
               times(1)).getView();
        verify(populatedAssetsScreen,
               never()).getView();
        verify(view).setContent(emptyAssetsScreen.getView().getElement());
        assertTrue(assetsScreen.isEmpty());
    }

    @Test
    public void testShowPopulatedScreenAssets() {
        when(libraryService.hasAssets(any(WorkspaceProject.class))).thenReturn(true);
        this.assetsScreen.init();
        verify(emptyAssetsScreen,
               never()).getView();
        verify(populatedAssetsScreen,
               times(1)).getView();
        verify(view).setContent(populatedAssetsScreen.getView().getElement());
        assertFalse(assetsScreen.isEmpty());
    }

    @Test
    public void testSetContentNotCalledWhenAlreadyDisplayed() throws Exception {
        try {
            testShowEmptyScreenAssets();
        } catch (AssertionError ae) {
            throw new AssertionError("Precondition failed. Could not set empty asset screen.",
                                     ae);
        }

        HTMLElement emptyElement = emptyAssetsScreen.getView().getElement();
        emptyElement.parentNode = mock(HTMLElement.class);
        reset(view);

        assetsScreen.init();
        verify(view,
               never()).setContent(any());
    }

    @Test
    public void testInvalidProject() throws Exception {
        reset(workspaceProject);
        doReturn(null).when(workspaceProject).getMainModule();

        assetsScreen.init();
        verify(view).setContent(invalidProjectScreen.getView().getElement());
        verify(libraryService,
               never()).hasAssets(any(WorkspaceProject.class));
    }

    @Test
    public void testRefreshWhenViewIsEmpty() {
        doNothing().when(assetsScreen).showAssets();
        when(assetsScreen.isEmpty()).thenReturn(true);
        assetsScreen.observeAddAsset(null);
        verify(assetsScreen).showAssets();
    }

    @Test
    public void doNotRefreshWhenViewIsPopulated() {
        when(assetsScreen.isEmpty()).thenReturn(false);
        assetsScreen.observeAddAsset(null);
        verify(assetsScreen,
               never()).showAssets();
    }
}