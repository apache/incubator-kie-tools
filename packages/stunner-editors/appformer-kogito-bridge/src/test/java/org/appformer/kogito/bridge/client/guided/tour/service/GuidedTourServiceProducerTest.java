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

package org.appformer.kogito.bridge.client.guided.tour.service;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedTourServiceProducerTest {

    private GuidedTourServiceProducer producer;

    @Mock
    private Console console;

    @Before
    public void setup() {
        producer = spy(new GuidedTourServiceProducer());
        DomGlobal.console = console;
    }

    @Test
    public void testProduceWhenEnvelopeIsAvailable() {
        doReturn(true).when(producer).isEnvelopeAvailable();

        final GuidedTourService guidedTourService = producer.produce();

        verifyNoMoreInteractions(console);
        assertNotNull(guidedTourService);
        assertNotEquals(DEFAULT, guidedTourService);
    }

    @Test
    public void testProduceWhenEnvelopeIsNotAvailable() {
        doReturn(false).when(producer).isEnvelopeAvailable();

        final GuidedTourService guidedTourService = producer.produce();

        verify(console).info("[GuidedTourServiceProducer] Envelope API is not available.");
        assertNotNull(guidedTourService);
        assertEquals(DEFAULT, guidedTourService);
    }
}
