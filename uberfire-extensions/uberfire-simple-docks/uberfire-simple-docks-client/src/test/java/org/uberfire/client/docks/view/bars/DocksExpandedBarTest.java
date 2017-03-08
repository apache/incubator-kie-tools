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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DocksExpandedBarTest {

    private DocksExpandedBar docksExpandedBar;
    private FlowPanel targetPanel;

    @Before
    public void setup() {
        DocksExpandedBar dock = new DocksExpandedBar(UberfireDockPosition.WEST);
        targetPanel = mock(FlowPanel.class);
        dock.targetPanel = targetPanel;
        docksExpandedBar = spy(dock);
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
}
