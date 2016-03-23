/*
 *
 *  * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.workbench;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DockLayoutPanel.class)
public class WorkbenchLayoutImplTest {

    private WorkbenchLayoutImpl workbenchLayout;

    private Widget widget;

    @Before
    public void setup() {
        workbenchLayout = new WorkbenchLayoutImpl();

        widget = mock(Widget.class, withSettings().extraInterfaces(RequiresResize.class));
        final Element element = mock(Element.class);
        when(element.getStyle()).thenReturn(mock(Style.class));
        when(widget.getElement()).thenReturn(element);
    }

    @Test
    public void testMaximize() {
        workbenchLayout.maximize(widget);

        verify(widget).addStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);

        verify(((RequiresResize) widget), never()).onResize();
    }

    @Test
    public void testExpandAnimation() {
        final WorkbenchLayoutImpl.ExpandAnimation expandAnimation = new WorkbenchLayoutImpl.ExpandAnimation(widget, Maps.<Widget, WorkbenchLayoutImpl.OriginalStyleInfo>newHashMap(), mock(SimpleLayoutPanel.class));

        expandAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
    }

    @Test
    public void testUnMaximize() {
        workbenchLayout.maximize(widget);
        workbenchLayout.unmaximize(widget);

        verify(widget).addStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);
        verify(widget).removeStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);

        verify(((RequiresResize) widget), never()).onResize();
    }

    @Test
    public void testCollapseAnimation() {
        final HashMap<Widget, WorkbenchLayoutImpl.OriginalStyleInfo> maximizedWidgetOriginalStyles = Maps.newHashMap();
        maximizedWidgetOriginalStyles.put(widget, new WorkbenchLayoutImpl.OriginalStyleInfo(widget));
        final WorkbenchLayoutImpl.CollapseAnimation collapseAnimation = new WorkbenchLayoutImpl.CollapseAnimation(widget, maximizedWidgetOriginalStyles);

        collapseAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
    }

}