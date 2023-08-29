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


package org.uberfire.client.docks.view;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DockResizeBarTest {

    private static final String POSITION_SHORT_NAME = "W";

    @Mock
    private Element elementMock;

    @Mock
    private UberfireDockPosition dockPositionMock;

    @Mock
    private DocksBar docksBarMock;

    private DockResizeBar dockResizeBar;

    @Before
    public void setUp() throws Exception {
        when(docksBarMock.getPosition()).thenReturn(dockPositionMock);
        when(dockPositionMock.getShortName()).thenReturn(POSITION_SHORT_NAME);

        dockResizeBar = spy(new DockResizeBar(docksBarMock));

        when(dockResizeBar.getElement()).thenReturn(elementMock);
    }

    @Test
    public void testOuiaComponentTypeAttribute() {
        assertEquals("resize-docks-bar", dockResizeBar.ouiaComponentType().getValue());
    }

    @Test
    public void testOuiaComponentIdAttribute() {
        assertEquals("resize-docks-bar-W", dockResizeBar.ouiaComponentId().getValue());
    }

    @Test
    public void testOuiaAttributeRenderer() {
        final OuiaComponentTypeAttribute componentTypeAttribute = dockResizeBar.ouiaComponentType();
        dockResizeBar.ouiaAttributeRenderer().accept(componentTypeAttribute);
        verify(elementMock).setAttribute(componentTypeAttribute.getName(), componentTypeAttribute.getValue());
    }
}
