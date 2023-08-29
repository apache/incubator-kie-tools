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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DocksExpandedBarTest {

    @Mock
    private Element elementMock;

    private DocksExpandedBar docksExpandedBar;
    private FlowPanel targetPanel;

    @Before
    public void setup() {
        DocksExpandedBar dock = new DocksExpandedBar(UberfireDockPosition.EAST);
        targetPanel = mock(FlowPanel.class);
        dock.targetPanel = targetPanel;
        docksExpandedBar = spy(dock);

        doReturn(elementMock).when(docksExpandedBar).getElement();
    }

    @Test
    public void resizeTest() {
        docksExpandedBar.onResize();
        verify(docksExpandedBar).resizeTargetPanel();
    }

    @Test
    public void resizeWithAnInvalidWidthShouldNeverSetupSizeOfTargetPanel() {
        doReturn(0).when(docksExpandedBar).calculateDockHeight();
        doReturn(0).when(docksExpandedBar).calculateDockWidth();

        docksExpandedBar.onResize();

        verify(targetPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
    }

    @Test
    public void resizeWithAValidWidthShouldNeverSetupSizeOfTargetPanel() {
        doReturn(10).when(docksExpandedBar).calculateDockHeight();
        doReturn(110).when(docksExpandedBar).calculateDockWidth();

        docksExpandedBar.onResize();

        verify(targetPanel,
               never()).setPixelSize(10,
                                     110);
    }

    @Test
    public void setPanelSizeWithAnInvalidWidthShouldNeverSetupSizeOfTargetPanel() {

        docksExpandedBar.setPanelSize(0,
                                      -1);

        verify(targetPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
    }

    @Test
    public void setPanelSizeAValidWidthShouldNeverSetupSizeOfTargetPanel() {

        docksExpandedBar.setPanelSize(1,
                                      10);

        verify(targetPanel).setPixelSize(1,
                                         10);
    }

    @Test
    public void testOuiaComponentTypeAttribute() {
        assertEquals("expanded-docks-bar", docksExpandedBar.ouiaComponentType().getValue());
    }

    @Test
    public void testOuiaComponentIdAttribute() {
        assertEquals("expanded-docks-bar-E", docksExpandedBar.ouiaComponentId().getValue());
    }

    @Test
    public void testOuiaAttributeRenderer() {
        final OuiaComponentTypeAttribute componentTypeAttribute = docksExpandedBar.ouiaComponentType();
        docksExpandedBar.ouiaAttributeRenderer().accept(componentTypeAttribute);
        verify(elementMock).setAttribute(componentTypeAttribute.getName(), componentTypeAttribute.getValue());
    }
}
