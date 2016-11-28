/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DataSourceEventHelperTest {

    @Mock
    private EventSourceMock< NewDataSourceEvent > newDataSourceEvent;

    @Mock
    private EventSourceMock< UpdateDataSourceEvent > updateDataSourceEvent;

    @Mock
    private EventSourceMock< DeleteDataSourceEvent > deleteDataSourceEvent;

    @Mock
    private EventSourceMock< NewDriverEvent > newDriverEvent;

    @Mock
    private EventSourceMock< UpdateDriverEvent > updateDriverEvent;

    @Mock
    private EventSourceMock< DeleteDriverEvent > deleteDriverEvent;

    @Mock
    private NewDataSourceEvent newDataSource;

    @Mock
    private UpdateDataSourceEvent updateDataSource;

    @Mock
    private DeleteDataSourceEvent deleteDataSource;

    @Mock
    private NewDriverEvent newDriver;

    @Mock
    private UpdateDriverEvent updateDriver;

    @Mock
    private DeleteDriverEvent deleteDriver;

    private DataSourceEventHelper eventHelper;

    @Before
    public void setup( ) {
        eventHelper = new DataSourceEventHelper( newDataSourceEvent, updateDataSourceEvent, deleteDataSourceEvent,
                newDriverEvent, updateDriverEvent, deleteDriverEvent );
    }

    @Test
    public void testCreateDataSourceEvent( ) {
        eventHelper.fireCreateEvent( newDataSource );
        verify( newDataSourceEvent, times( 1 ) ).fire( newDataSource );
    }

    @Test
    public void testUpdateDataSourceEvent( ) {
        eventHelper.fireUpdateEvent( updateDataSource );
        verify( updateDataSourceEvent, times( 1 ) ).fire( updateDataSource );
    }

    @Test
    public void testDeleteDataSourceEvent( ) {
        deleteDataSourceEvent.fire( deleteDataSource );
        verify( deleteDataSourceEvent, times( 1 ) ).fire( deleteDataSource );
    }

    @Test
    public void testCreateDriverEvent( ) {
        eventHelper.fireCreateEvent( newDriver );
        verify( newDriverEvent, times( 1 ) ).fire( newDriver );
    }

    @Test
    public void testUpdateDriverEvent( ) {
        eventHelper.fireUpdateEvent( updateDriver );
        verify( updateDriverEvent, times( 1 ) ).fire( updateDriver );
    }

    @Test
    public void testDeleteDriverEvent( ) {
        eventHelper.fireDeleteEvent( deleteDriver );
        verify( deleteDriverEvent, times( 1 ) ).fire( deleteDriver );
    }
}