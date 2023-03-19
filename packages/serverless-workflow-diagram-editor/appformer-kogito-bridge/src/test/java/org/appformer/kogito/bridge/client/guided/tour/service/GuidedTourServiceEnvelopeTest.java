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
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedTourServiceEnvelopeTest {

    private GuidedTourServiceEnvelope envelope;

    @Mock
    private GuidedTourServiceNativeEnvelope nativeEnvelope;

    @Before
    public void setup() {
        envelope = spy(new GuidedTourServiceEnvelope());
        doReturn(nativeEnvelope).when(envelope).getNativeEnvelope();
    }

    @Test
    public void testRefresh() {
        final UserInteraction userInteraction = mock(UserInteraction.class);
        envelope.refresh(userInteraction);
        verify(nativeEnvelope).refresh(userInteraction);
    }

    @Test
    public void testRegisterTutorial() {
        final Tutorial tutorial = mock(Tutorial.class);
        envelope.registerTutorial(tutorial);
        verify(nativeEnvelope).registerTutorial(tutorial);
    }

    @Test
    public void testIsEnabledWhenItReturnsTrue() {
        when(nativeEnvelope.isEnabled()).thenReturn(true);
        assertTrue(envelope.isEnabled());
    }

    @Test
    public void testIsEnabledWhenItReturnsFalse() {
        when(nativeEnvelope.isEnabled()).thenReturn(false);
        assertFalse(envelope.isEnabled());
    }
}
