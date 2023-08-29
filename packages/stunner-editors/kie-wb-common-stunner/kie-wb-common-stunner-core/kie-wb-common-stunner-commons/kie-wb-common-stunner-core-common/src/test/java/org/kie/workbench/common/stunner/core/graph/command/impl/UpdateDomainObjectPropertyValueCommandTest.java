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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingSimpleDomainObject;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UpdateDomainObjectPropertyValueCommandTest extends AbstractGraphCommandTest {

    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_OLD_VALUE = "oldValue";

    private UpdateDomainObjectPropertyValueCommand command;
    private TestingSimpleDomainObject domainObject;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        domainObject = new TestingSimpleDomainObject(testingGraphMockHandler);
        when(propertyAdapter.getValue(domainObject.getNameProperty())).thenReturn(PROPERTY_OLD_VALUE);

        this.command = new UpdateDomainObjectPropertyValueCommand(domainObject,
                                                                  TestingSimpleDomainObject.NAME,
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

        verify(propertyAdapter).getValue(eq(domainObject.getNameProperty()));
        verify(propertyAdapter).setValue(eq(domainObject.getNameProperty()), eq(PROPERTY_VALUE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo() {
        command.execute(graphCommandExecutionContext);

        verify(propertyAdapter).getValue(eq(domainObject.getNameProperty()));
        verify(propertyAdapter).setValue(eq(domainObject.getNameProperty()),
                                         eq(PROPERTY_VALUE));

        assertEquals(CommandResult.Type.INFO,
                     command.undo(graphCommandExecutionContext).getType());

        //One read for the execute, one read for the undo. Resetting the mock would require it to be setup again.
        verify(propertyAdapter, times(2)).getValue(eq(domainObject.getNameProperty()));
        verify(propertyAdapter).setValue(eq(domainObject.getNameProperty()),
                                         eq(PROPERTY_OLD_VALUE));
    }
}
