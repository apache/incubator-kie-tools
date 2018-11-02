/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader.activityRefOf;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader.errorRefOf;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader.escalationRefOf;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader.messageRefOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventDefinitionReaderTest {

    private static final String SOME_VALUE = "SOME_VALUE";

    @Mock
    private ErrorEventDefinition errorEventDefinition;

    @Mock
    private MessageEventDefinition messageEventDefinition;

    @Mock
    private EscalationEventDefinition escalationEventDefinition;

    @Mock
    private CompensateEventDefinition compensateEventDefinition;

    @Test
    public void testErrorRefOfWithValue() {
        Error error = mock(Error.class);
        when(error.getErrorCode()).thenReturn(SOME_VALUE);
        testErrorRef(error, SOME_VALUE);
    }

    @Test
    public void testErrorRefOfWithNOValue() {
        testErrorRef(null, "");
    }

    private void testErrorRef(Error error, String expectedValue) {
        when(errorEventDefinition.getErrorRef()).thenReturn(error);
        assertEquals(expectedValue, errorRefOf(errorEventDefinition));
    }

    @Test
    public void testMessageRefOfWithValue() {
        Message message = mock(Message.class);
        when(message.getName()).thenReturn(SOME_VALUE);
        testMessageRefOf(message, SOME_VALUE);
    }

    @Test
    public void testMessageRefOfWithNoValue() {
        testMessageRefOf(null, "");
    }

    private void testMessageRefOf(Message message, String expectedValue) {
        when(messageEventDefinition.getMessageRef()).thenReturn(message);
        assertEquals(expectedValue, messageRefOf(messageEventDefinition));
    }

    @Test
    public void testEscalationRefOfWithValue() {
        Escalation escalation = mock(Escalation.class);
        when(escalation.getEscalationCode()).thenReturn(SOME_VALUE);
        testEscalationRefOf(escalation, SOME_VALUE);
    }

    @Test
    public void testEscalationRefOfWithNoValue() {
        testEscalationRefOf(null, "");
    }

    private void testEscalationRefOf(Escalation escalation, String expectedValue) {
        when(escalationEventDefinition.getEscalationRef()).thenReturn(escalation);
        assertEquals(expectedValue, escalationRefOf(escalationEventDefinition));
    }

    @Test
    public void testActivityRefOfWithValue() {
        Activity activity = mock(Activity.class);
        when(activity.getId()).thenReturn(SOME_VALUE);
        testActivityRefOf(activity, SOME_VALUE);
    }

    @Test
    public void testActivityRefOfWithNoValue() {
        testActivityRefOf(null, null);
    }

    private void testActivityRefOf(Activity activity, String expectedValue) {
        when(compensateEventDefinition.getActivityRef()).thenReturn(activity);
        assertEquals(expectedValue, activityRefOf(compensateEventDefinition));
    }
}
