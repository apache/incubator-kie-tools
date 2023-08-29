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


package org.uberfire.client.docks.view.bars;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocksCollapsedBarTest {

    private static final String POSITION_SHORT_NAME = "W";

    @Mock
    private Element elementMock;

    @Mock
    private UberfireDockPosition dockPositionMock;

    private DocksCollapsedBar docksCollapsedBar;

    @Before
    public void setUp() throws Exception {
        when(dockPositionMock.getShortName()).thenReturn(POSITION_SHORT_NAME);

        docksCollapsedBar = spy(new DocksCollapsedBar(dockPositionMock));

        doReturn(elementMock).when(docksCollapsedBar).getElement();
    }

    @Test
    public void testOuiaComponentTypeAttribute() {
        assertEquals("collapsed-docks-bar", docksCollapsedBar.ouiaComponentType().getValue());
    }

    @Test
    public void testOuiaComponentIdAttribute() {
        assertEquals("collapsed-docks-bar-W", docksCollapsedBar.ouiaComponentId().getValue());
    }

    @Test
    public void testOuiaAttributeRenderer() {
        final OuiaComponentTypeAttribute componentTypeAttribute = docksCollapsedBar.ouiaComponentType();
        docksCollapsedBar.ouiaAttributeRenderer().accept(componentTypeAttribute);
        verify(elementMock).setAttribute(componentTypeAttribute.getName(), componentTypeAttribute.getValue());
    }
}
