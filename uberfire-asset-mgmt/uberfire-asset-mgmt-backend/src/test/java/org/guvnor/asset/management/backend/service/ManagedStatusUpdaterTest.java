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

package org.guvnor.asset.management.backend.service;

import javax.enterprise.event.Event;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManagedStatusUpdaterTest {

    @Mock
    private Repository originalRepository;

    @Mock
    private Repository updatedRepository;

    @Mock
    private RepositoryService repositoryService;

    private Event<RepositoryEnvironmentUpdatedEvent> repositoryUpdatedEvent;

    private ManagedStatusUpdater managedStatusUpdater;

    @Captor
    ArgumentCaptor<RepositoryEnvironmentConfigurations> repositoryEnvironmentConfigurationsCaptor;

    private RepositoryEnvironmentUpdatedEvent updatedEvent;

    @Before
    public void setUp() throws Exception {
        repositoryUpdatedEvent = new EventSourceMock<RepositoryEnvironmentUpdatedEvent>() {
            @Override
            public void fire(final RepositoryEnvironmentUpdatedEvent event) {
                ManagedStatusUpdaterTest.this.updatedEvent = event;
            }
        };
        when(repositoryService.updateRepositoryConfiguration(eq(originalRepository),
                                                             any(RepositoryEnvironmentConfigurations.class)))
                .thenReturn(updatedRepository);

        managedStatusUpdater = new ManagedStatusUpdater(repositoryService,
                                                        repositoryUpdatedEvent);
    }

    @Test
    public void testMakeManaged() throws Exception {
        managedStatusUpdater.updateManagedStatus(originalRepository,
                                                 true);
        verify(repositoryService).updateRepositoryConfiguration(eq(originalRepository),
                                                                repositoryEnvironmentConfigurationsCaptor.capture());
        assertTrue(repositoryEnvironmentConfigurationsCaptor.getValue().isManaged());
    }

    @Test
    public void testMakeNotManaged() throws Exception {
        managedStatusUpdater.updateManagedStatus(originalRepository,
                                                 false);
        verify(repositoryService).updateRepositoryConfiguration(eq(originalRepository),
                                                                repositoryEnvironmentConfigurationsCaptor.capture());
        assertFalse(repositoryEnvironmentConfigurationsCaptor.getValue().isManaged());
    }

    @Test
    public void testUseUpdatedRepositoryForTheEvent() throws Exception {
        managedStatusUpdater.updateManagedStatus(originalRepository,
                                                 true);

        assertEquals(updatedRepository,
                     updatedEvent.getUpdatedRepository());
    }

    @Test
    public void testReturnsUpdatedRepository() throws Exception {
        assertEquals(updatedRepository,
                     managedStatusUpdater.updateManagedStatus(originalRepository,
                                                              true));
    }
}