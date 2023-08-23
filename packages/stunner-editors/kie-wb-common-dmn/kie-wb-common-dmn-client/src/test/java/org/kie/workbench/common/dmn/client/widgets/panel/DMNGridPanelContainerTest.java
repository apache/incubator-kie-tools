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

package org.kie.workbench.common.dmn.client.widgets.panel;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNGridPanelContainerTest {

    private static final int WIDTH = 100;

    private static final int HEIGHT = 200;

    @Mock
    private com.google.gwt.user.client.Element containerElement;

    @Mock
    private com.google.gwt.user.client.Element containerParentElement;

    private DMNGridPanelContainer container;

    @Before
    public void setup() {
        this.container = spy(new DMNGridPanelContainer());

        when(container.getElement()).thenReturn(containerElement);
        when(containerElement.getParentElement()).thenReturn(containerParentElement);
        when(containerParentElement.getOffsetWidth()).thenReturn(WIDTH);
        when(containerParentElement.getOffsetHeight()).thenReturn(HEIGHT);
    }

    @Test
    public void testSetWidgetNotResizableAndResize() {
        final Widget widget = mock(Widget.class);

        container.setWidget(widget);

        verifyNotResizableInteractions(widget);
    }

    @Test
    public void testSetIsWidgetNotResizableAndResize() {
        final IsWidget widget = mock(IsWidget.class);

        container.setWidget(widget);

        verifyNotResizableInteractions(widget);
    }

    @Test
    public void testSetWidgetIsResizableAndResize() {
        final ResizeComposite widget = mock(ResizeComposite.class);

        container.setWidget(widget);

        verifyResizableInteractions(widget);
    }

    @Test
    public void testSetWidgetIsResizableAndResizeWithZeroWidth() {
        when(containerParentElement.getOffsetWidth()).thenReturn(0);

        final ResizeComposite widget = mock(ResizeComposite.class);

        container.setWidget(widget);

        verifyResizableInteractionsWithZeroDimension(widget);
    }

    @Test
    public void testSetWidgetIsResizableAndResizeWithZeroHeight() {
        when(containerParentElement.getOffsetHeight()).thenReturn(0);

        final ResizeComposite widget = mock(ResizeComposite.class);

        container.setWidget(widget);

        verifyResizableInteractionsWithZeroDimension(widget);
    }

    private void verifyNotResizableInteractions(final IsWidget widget) {
        container.onResize();

        verify(container).setPixelSize(WIDTH, HEIGHT);
        verifyZeroInteractions(widget);
    }

    private void verifyResizableInteractions(final RequiresResize widget) {
        container.onResize();

        verify(container).setPixelSize(WIDTH, HEIGHT);
        verify(widget).onResize();
    }

    private void verifyResizableInteractionsWithZeroDimension(final RequiresResize widget) {
        container.onResize();

        verify(container, never()).setPixelSize(anyInt(), anyInt());
        verify(widget).onResize();
    }
}
