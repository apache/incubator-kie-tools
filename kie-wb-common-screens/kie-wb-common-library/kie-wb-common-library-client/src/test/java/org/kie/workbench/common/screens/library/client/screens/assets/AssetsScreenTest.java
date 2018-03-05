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

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

import elemental2.dom.HTMLElement;

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
    private TranslationService ts;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private LibraryService libraryService;

    @Before
    public void setUp() {

        WorkspaceProject projectInfo = mock(WorkspaceProject.class);
        when(libraryPlaces.getActiveWorkspaceContext()).thenReturn(projectInfo);

        EmptyAssetsView emptyView = mock(EmptyAssetsView.class);
        PopulatedAssetsView populatedView = mock(PopulatedAssetsView.class);

        HTMLElement emptyElement = mock(HTMLElement.class);
        HTMLElement populatedElement = mock(HTMLElement.class);

        when(emptyAssetsScreen.getView()).thenReturn(emptyView);
        when(emptyView.getElement()).thenReturn(emptyElement);

        when(populatedAssetsScreen.getView()).thenReturn(populatedView);
        when(populatedView.getElement()).thenReturn(populatedElement);

        this.assetsScreen = new AssetsScreen(view,
                                             libraryPlaces,
                                             emptyAssetsScreen,
                                             populatedAssetsScreen,
                                             ts,
                                             busyIndicatorView,
                                             new CallerMock<>(libraryService));
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
    }

    @Test
    public void testSetContentNotCalledWhenAlreadyDisplayed() throws Exception {
        try {
            testShowEmptyScreenAssets();
        } catch (AssertionError ae) {
            throw new AssertionError("Precondition failed. Could not set empty asset screen.", ae);
        }

        HTMLElement emptyElement = emptyAssetsScreen.getView().getElement();
        emptyElement.parentNode = mock(HTMLElement.class);
        reset(view);

        assetsScreen.init();
        verify(view, never()).setContent(any());
    }
}