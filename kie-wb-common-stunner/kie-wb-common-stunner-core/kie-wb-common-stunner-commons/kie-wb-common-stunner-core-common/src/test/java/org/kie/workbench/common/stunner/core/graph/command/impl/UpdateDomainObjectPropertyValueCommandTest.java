/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateDomainObjectPropertyValueCommandTest extends AbstractGraphCommandTest {

    private static final String PROPERTY = "property";

    private static final String PROPERTY_FIELD = "property.name";

    private static final String NAME_FIELD = "name";

    private static final String PROPERTY_VALUE = "value";

    private static final String PROPERTY_OLD_VALUE = "oldValue";

    @Mock
    private DomainObject domainObject;

    private UpdateDomainObjectPropertyValueCommand command;

    @Mock
    private Object property;

    @Mock
    private Object nameProperty;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(definitionAdapter.getProperty(domainObject, PROPERTY)).thenReturn(Optional.of(property));
        when(definitionAdapter.getProperty(property, NAME_FIELD)).thenReturn(Optional.of(nameProperty));
        when(propertyAdapter.getValue(nameProperty)).thenReturn(PROPERTY_OLD_VALUE);

        this.command = new UpdateDomainObjectPropertyValueCommand(domainObject,
                                                                  PROPERTY_FIELD,
                                                                  PROPERTY_VALUE);
    }

    @Test
    public void testAllow() {
        assertEquals(CommandResult.Type.INFO,
                     command.allow(graphCommandExecutionContext).getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        assertEquals(CommandResult.Type.INFO,
                     command.execute(graphCommandExecutionContext).getType());

        verify(propertyAdapter).getValue(eq(nameProperty));
        verify(propertyAdapter).setValue(eq(nameProperty), eq(PROPERTY_VALUE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo() {
        command.execute(graphCommandExecutionContext);

        verify(propertyAdapter).getValue(eq(nameProperty));
        verify(propertyAdapter).setValue(eq(nameProperty),
                                         eq(PROPERTY_VALUE));

        assertEquals(CommandResult.Type.INFO,
                     command.undo(graphCommandExecutionContext).getType());

        //One read for the execute, one read for the undo. Resetting the mock would require it to be setup again.
        verify(propertyAdapter, times(2)).getValue(eq(nameProperty));
        verify(propertyAdapter).setValue(eq(nameProperty),
                                         eq(PROPERTY_OLD_VALUE));
    }
}
