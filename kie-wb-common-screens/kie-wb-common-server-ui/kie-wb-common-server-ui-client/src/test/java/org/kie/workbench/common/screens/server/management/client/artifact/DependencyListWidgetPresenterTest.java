/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.artifact;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyListWidgetPresenterTest {

    private DependencyListWidgetPresenter presenter;

    @Mock
    private DependencyListWidgetPresenter.View view;

    @Mock
    private ArtifactListPresenter artifactListPresenter;

    @Mock
    private EventSourceMock<DependencyPathSelectedEvent> event;

    @Before
    public void setup() {
        presenter = new DependencyListWidgetPresenter( view, artifactListPresenter, event );

        assertEquals( view, presenter.getView() );
        assertEquals( artifactListPresenter, presenter.getArtifactListPresenter() );
    }

    @Test
    public void testSelect() {
        presenter.onSelect( "my_selected_artifact" );

        verify( event, times( 1 ) ).fire( any( DependencyPathSelectedEvent.class ) );
    }

    @Test
    public void testSearch() {
        presenter.search( "artifact" );

        verify( artifactListPresenter, times( 1 ) ).search( "artifact" );
    }

    @Test
    public void testRefresh() {
        presenter.refresh();

        verify( artifactListPresenter, times( 1 ) ).refresh();
    }
}
