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


package org.kie.workbench.common.stunner.forms.client.screens;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramEditorPropertiesScreenViewTest {

    @Mock
    private FlowPanel widget;

    @Mock
    private FlowPanel loadingPanel;

    @Mock
    private FlowPanel widgetPanel;

    private DiagramEditorPropertiesScreenView tested;

    @Before
    public void setup() throws Exception {
        this.tested = new DiagramEditorPropertiesScreenView(loadingPanel,
                                                            widgetPanel);
    }

    @Test
    public void testSetWidget() {
        tested.setWidget(widget);
        verify(widgetPanel,
               times(1)).clear();
        verify(widgetPanel,
               times(1)).add(any(IsWidget.class));
    }

    @Test
    public void testShowLoading() {
        tested.showLoading();
        verify(widgetPanel,
               times(1)).setVisible(eq(false));
        verify(loadingPanel,
               times(1)).setVisible(eq(true));
    }

    @Test
    public void testHideLoading() {
        tested.hideLoading();
        verify(loadingPanel,
               times(1)).setVisible(eq(false));
        verify(widgetPanel,
               times(1)).setVisible(eq(true));
    }
}
