/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class ThrowEventPropertyWriterTest extends EventPropertyWriterTest {

    @Before
    public void init() {
        event = bpmn2.createEndEvent();
        event.setId(elementId);
        propertyWriter = spy(new ThrowEventPropertyWriter((EndEvent) event,
                                                          new FlatVariableScope(),
                                                          new HashSet<>()));
    }

    @Override
    public ErrorEventDefinition getErrorDefinition() {
        return (ErrorEventDefinition) ((EndEvent) event).getEventDefinitions().get(0);
    }
}
