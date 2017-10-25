/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget.artifact;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.ui.client.widget.artifact.ArtifactSelectorPresenter.SEARCH_ALL_FILTER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactSelectorPresenterTest {

    private static final String VALUE = "VALUE";

    @Mock
    private ArtifactListPresenter artifactListPresenter;

    @Mock
    private ArtifactListView artifactListView;

    @Mock
    private ArtifactSelectorPresenter.View view;

    private ArtifactSelectorPresenter presenter;

    @Mock
    private ArtifactSelectorPresenter.ArtifactSelectHandler artifactSelectHandler;

    @Before
    public void setUp() {
        when(artifactListPresenter.getView()).thenReturn(artifactListView);
        presenter = new ArtifactSelectorPresenter(view,
                                                  artifactListPresenter);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(artifactListPresenter,
               times(1)).notifyOnRefresh(false);
        verify(artifactListPresenter,
               times(1)).setup(ColumnType.GAV);
        verify(artifactListPresenter,
               times(1)).search(SEARCH_ALL_FILTER,
                                presenter.FORMATS);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view,
               times(1)).clear();
        verify(artifactListPresenter,
               times(1)).search(SEARCH_ALL_FILTER,
                                presenter.FORMATS);
    }

    @Test
    public void testRefresh() {
        presenter.refresh();
        verify(artifactListPresenter,
               times(1)).refresh();
    }

    @Test
    public void testGetArtifactListView() {
        assertEquals(artifactListView,
                     presenter.getArtifactListView());
    }

    @Test
    public void testOnArtifactSelected() {
        presenter.setArtifactSelectHandler(artifactSelectHandler);
        presenter.onArtifactSelected(VALUE);
        verify(artifactSelectHandler,
               times(1)).onArtifactSelected(VALUE);
    }

    @Test
    public void testOnSearch() {
        when(view.getFilter()).thenReturn(VALUE);
        presenter.onSearch();
        verify(artifactListPresenter,
               times(1)).search(VALUE,
                                presenter.FORMATS);
    }
}
