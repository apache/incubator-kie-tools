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

package org.kie.workbench.common.screens.server.management.client.widget.artifact;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactListWidgetPresenterTest {

    private List<String> FORMATS = Arrays.asList("jar");

    @Mock
    ArtifactListWidgetPresenter.View view;

    @Mock
    ArtifactListPresenter artifactListPresenter;

    @Mock
    Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent;

    @InjectMocks
    ArtifactListWidgetPresenter presenter;

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
        assertEquals( artifactListPresenter.getView(), presenter.getArtifactListView() );
        verify( artifactListPresenter ).notifyOnRefresh( false );
        verify( artifactListPresenter ).setup( ColumnType.GAV );
        verify( artifactListPresenter ).search( "", FORMATS );
    }

    @Test
    public void testSearch() {
        presenter.search( "something" );

        verify( artifactListPresenter ).search( "something", FORMATS );
    }

    @Test
    public void testRefresh() {
        presenter.refresh();

        verify( artifactListPresenter ).refresh();
    }

    @Test
    public void testOnSelect() {
        presenter.onSelect( "some path" );

        verify( dependencyPathSelectedEvent ).fire( new DependencyPathSelectedEvent( presenter, "some path" ) );
    }
}