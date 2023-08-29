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


package org.uberfire.client.docks.view.items;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDockItemOuiaTest {

    @Mock
    private Element elementMock;

    @Mock
    private UberfireDock dockMock;

    @Before
    public void setUp() throws Exception {
        when(dockMock.getIdentifier()).thenReturn("xyz");
    }

    @Test
    public void testSideDockItems() {
        when(dockMock.getDockPosition()).thenReturn(UberfireDockPosition.EAST);
        assertOuiaCompliance(new SideDockItem(dockMock,
                                              mock(ParameterizedCommand.class),
                                              mock(ParameterizedCommand.class)));

        assertOuiaCompliance(new SingleSideDockItem(dockMock,
                                                    mock(ParameterizedCommand.class)));
    }

    private void assertOuiaCompliance(AbstractDockItem dockItem) {
        dockItem = spy(dockItem);
        doReturn(elementMock).when(dockItem).getElement();

        final OuiaComponentTypeAttribute componentTypeAttribute = dockItem.ouiaComponentType();

        assertEquals("docks-item", componentTypeAttribute.getValue());

        assertEquals("docks-item-xyz", dockItem.ouiaComponentId().getValue());

        dockItem.ouiaAttributeRenderer().accept(componentTypeAttribute);
        verify(elementMock).setAttribute(componentTypeAttribute.getName(), componentTypeAttribute.getValue());

        reset(elementMock);
    }
}
