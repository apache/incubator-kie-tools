/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.client.external;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.external.ExternalDataCallbackCoordinator.QueuedDataSetReadyCallback;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExternalDataCallbackCoordinatorTest {

    private ExternalDataCallbackCoordinator coordinator;
    private DataSetDef def;
    private DataSetReadyCallback c1;
    private DataSetReadyCallback c2;

    @Before
    public void setup() {
        coordinator = new ExternalDataCallbackCoordinator();
        coordinator.setup();

        c1 = mock(DataSetReadyCallback.class);
        c2 = mock(DataSetReadyCallback.class);

        def = mock(DataSetDef.class);
        DomGlobal.console = Mockito.mock(Console.class);
    }

    @Test
    public void testOnDataSet() {
        var dataset = mock(DataSet.class);
        var queuedCallback = (QueuedDataSetReadyCallback) coordinator.getCallback(def, c1, e -> {
        }, () -> {
        });
        coordinator.getCallback(def, c2, e -> {
        }, () -> {
        });
        assertEquals(1, coordinator.queueMap.size());
        assertEquals(2, queuedCallback.queue.size());

        queuedCallback.callback(dataset);

        verify(c1).callback(any());
        verify(c2).callback(any());

        assertEquals(0, queuedCallback.queue.size());
        assertEquals(0, coordinator.queueMap.size());
    }

    @Test
    public void testOnError() {
        var c1 = mock(DataSetReadyCallback.class);
        var c2 = mock(DataSetReadyCallback.class);
        var error = mock(ClientRuntimeError.class);
        var queuedCallback = (QueuedDataSetReadyCallback) coordinator.getCallback(def, c1, e -> {
        }, () -> {
        });

        coordinator.getCallback(def, c2, e -> {
        }, () -> {
        });

        assertEquals(2, queuedCallback.queue.size());

        queuedCallback.onError(error);

        verify(c1).onError(any());
        verify(c2).onError(any());

        assertEquals(0, queuedCallback.queue.size());
        assertEquals(0, coordinator.queueMap.size());
    }

    @Test
    public void testNotFound() {
        var queuedCallback = (QueuedDataSetReadyCallback) coordinator.getCallback(def, c1, e -> {
        }, () -> {
        });

        coordinator.getCallback(def, c2, e -> {
        }, () -> {
        });

        assertEquals(queuedCallback.queue.size(), 2);

        queuedCallback.notFound();

        verify(c1).notFound();
        verify(c2).notFound();

        assertEquals(0, queuedCallback.queue.size());
        assertEquals(0, coordinator.queueMap.size());
    }

    @Test
    public void testCallbackOnError() {
        var dataset = mock(DataSet.class);
        var queuedCallback = (QueuedDataSetReadyCallback) coordinator.getCallback(def, c1, e -> {
        }, () -> {
        });

        doThrow(new RuntimeException()).when(c1).callback(any());

        coordinator.getCallback(def, c2, e -> {
        }, () -> {
        });
        assertEquals(1, coordinator.queueMap.size());
        assertEquals(queuedCallback.queue.size(), 2);

        queuedCallback.callback(dataset);

        verify(c1).callback(any());
        verify(c2).callback(any());

        assertEquals(0, queuedCallback.queue.size());
        assertEquals(0, coordinator.queueMap.size());
    }

}
