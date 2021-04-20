/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour;

import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourObserverTest {

    @Mock
    private Disposer<GuidedTourObserverFake> selfDisposer;

    @Mock
    private GuidedTourBridge bridge;

    private GuidedTourObserverFake observer;

    @Before
    public void setup() {
        observer = new GuidedTourObserverFake(selfDisposer);
    }

    @Test
    public void testDispose() {
        observer.dispose();
        verify(selfDisposer).dispose(observer);
    }

    @Test
    public void testSetMonitorBridge() {
        observer.setMonitorBridge(bridge);
        assertTrue(observer.getMonitorBridge().isPresent());
        assertEquals(bridge, observer.getMonitorBridge().get());
    }

    class GuidedTourObserverFake extends GuidedTourObserver<GuidedTourObserverFake> {

        GuidedTourObserverFake(final Disposer<GuidedTourObserverFake> selfDisposer) {
            super(selfDisposer);
        }
    }
}
