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

package org.kie.workbench.common.stunner.client.widgets.views.session;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScreenPanelViewImplTest {

    private static int WIDTH = 10;

    private static int HEIGHT = 20;

    @Mock
    private Widget parent;

    private ScreenPanelViewImpl.SizedResizeFlowPanel panel;

    private ScreenPanelView view;

    @Before
    public void setup() {
        this.panel = spy(new ScreenPanelViewImpl.SizedResizeFlowPanel());
        this.view = new ScreenPanelViewImpl(panel);

        doReturn(parent).when(panel).getParent();
    }

    @Test
    public void testOnResize() {
        when(parent.getOffsetWidth()).thenReturn(WIDTH);
        when(parent.getOffsetHeight()).thenReturn(HEIGHT);

        ((ResizeFlowPanel) view.asWidget()).onResize();

        verify(panel).setPixelSize(WIDTH, HEIGHT);
        verify(panel).doSuperOnResize();
    }
}
