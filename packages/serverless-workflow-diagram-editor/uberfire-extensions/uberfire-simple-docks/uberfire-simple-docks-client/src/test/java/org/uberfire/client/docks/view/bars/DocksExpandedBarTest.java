/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.docks.view.bars;

import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FlowPanel;
import org.junit.Ignore;
import org.junit.Test;
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

//@RunWith(GwtMockitoTestRunner.class)
public class DocksExpandedBarTest {

    @Mock
    private Element elementMock;

    private DocksExpandedBar docksExpandedBar;
    private FlowPanel targetPanel;

    //@Before
    public void setup() {
        DocksExpandedBar dock = new DocksExpandedBar(UberfireDockPosition.WEST);
        targetPanel = mock(FlowPanel.class);
        dock.targetPanel = targetPanel;
        docksExpandedBar = spy(dock);

        doReturn(elementMock).when(docksExpandedBar).getElement();
    }

    @Test
    @Ignore
    public void resizeTest() {
        docksExpandedBar.onResize();
        verify(docksExpandedBar).resizeTargetPanel();
    }

    @Test
    @Ignore
    public void resizeWithAnInvalidWidthShouldNeverSetupSizeOfTargetPanel() {
        doReturn(0).when(docksExpandedBar).calculateDockHeight();
        doReturn(0).when(docksExpandedBar).calculateDockWidth();

        docksExpandedBar.onResize();

        verify(targetPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
    }

    @Test
    @Ignore
    public void resizeWithAValidWidthShouldNeverSetupSizeOfTargetPanel() {
        doReturn(10).when(docksExpandedBar).calculateDockHeight();
        doReturn(110).when(docksExpandedBar).calculateDockWidth();

        docksExpandedBar.onResize();

        verify(targetPanel,
               never()).setPixelSize(10,
                                     110);
    }

    @Test
    @Ignore
    public void setPanelSizeWithAnInvalidWidthShouldNeverSetupSizeOfTargetPanel() {

        docksExpandedBar.setPanelSize(0,
                                      -1);

        verify(targetPanel,
               never()).setPixelSize(anyInt(),
                                     anyInt());
    }

    @Test
    @Ignore
    public void setPanelSizeAValidWidthShouldNeverSetupSizeOfTargetPanel() {

        docksExpandedBar.setPanelSize(1,
                                      10);

        verify(targetPanel).setPixelSize(1,
                                         10);
    }

    @Test
    @Ignore
    public void testOuiaComponentTypeAttribute() {
        assertEquals("expanded-docks-bar", docksExpandedBar.ouiaComponentType().getValue());
    }

    @Test
    @Ignore
    public void testOuiaComponentIdAttribute() {
        assertEquals("expanded-docks-bar-W", docksExpandedBar.ouiaComponentId().getValue());
    }

    @Test
    @Ignore
    public void testOuiaAttributeRenderer() {
        final OuiaComponentTypeAttribute componentTypeAttribute = docksExpandedBar.ouiaComponentType();
        docksExpandedBar.ouiaAttributeRenderer().accept(componentTypeAttribute);
        verify(elementMock).setAttribute(componentTypeAttribute.getName(), componentTypeAttribute.getValue());
    }
}
