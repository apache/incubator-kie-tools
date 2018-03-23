/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.impl;

import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.cluster.ClusterLibraryEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClusterLibraryObserverTest {

    @Mock
    private ClusterService clusterService;

    @Mock
    EventSourceMock<ClusterLibraryEvent> clusterLibraryEvent;

    private ClusterLibraryObserver clusterLibraryObserver;

    @Before
    public void setup() {

        clusterLibraryObserver = spy(new ClusterLibraryObserver(clusterService,
                                                                clusterLibraryEvent));
    }

    @Test
    public void onSystemRepositoryChangedEventShouldBeTranslatedToAClusterEvent() {
        when(clusterService.isAppFormerClustered()).thenReturn(true);

        clusterLibraryObserver.onSystemRepositoryChangedEvent(new SystemRepositoryChangedEvent());
        verify(clusterLibraryEvent).fire(any());
    }

    @Test
    public void onSystemRepositoryChangedEventShouldNotBeTranslatedToAClusterEvent() {
        when(clusterService.isAppFormerClustered()).thenReturn(false);

        clusterLibraryObserver.onSystemRepositoryChangedEvent(new SystemRepositoryChangedEvent());
        verify(clusterLibraryEvent,
               never()).fire(any());
    }
}