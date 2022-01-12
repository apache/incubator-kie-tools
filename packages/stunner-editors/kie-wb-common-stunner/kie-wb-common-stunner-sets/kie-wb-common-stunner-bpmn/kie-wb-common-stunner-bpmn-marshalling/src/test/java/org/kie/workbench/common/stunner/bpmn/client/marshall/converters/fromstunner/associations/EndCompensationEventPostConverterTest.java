/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations;

import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.EndCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EndCompensationEventPostConverterTest
        extends AbstractThrowCompensationEventPostConverterTest<EndCompensationEvent> {

    @Mock
    private EndCompensationEvent endCompensationEvent;

    @Override
    public EndCompensationEvent createEvent() {
        CompensationEventExecutionSet executionSet = new CompensationEventExecutionSet();
        when(endCompensationEvent.getExecutionSet()).thenReturn(executionSet);
        return endCompensationEvent;
    }

    @Override
    public CompensationEventExecutionSet getExecutionSet(EndCompensationEvent event) {
        return event.getExecutionSet();
    }

    @Override
    public PostConverterProcessor createConverter() {
        return new EndCompensationEventPostConverter();
    }
}
