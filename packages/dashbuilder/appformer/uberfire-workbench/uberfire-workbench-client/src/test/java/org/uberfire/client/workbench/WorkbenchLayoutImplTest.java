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
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.WorkbenchProfileCssClass;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DockLayoutPanel.class)
public class WorkbenchLayoutImplTest {

    private WorkbenchLayoutImpl workbenchLayout;
    private Widget widget;
    @Mock
    private SyncBeanManager iocManager;
    @Mock
    private SimpleLayoutPanel root;

    @Before
    public void setup() {
        workbenchLayout = new WorkbenchLayoutImpl(iocManager) {

            @Override
            ElementWrapperWidget<?> createWidgetFrom(HTMLElement h) {
                return mock(ElementWrapperWidget.class);
            }
        };
        workbenchLayout.perspectiveRootContainer = root;
        widget = mock(Widget.class,
                withSettings().extraInterfaces(RequiresResize.class));
        final Element element = mock(Element.class);
        when(element.getStyle()).thenReturn(mock(Style.class));
        when(widget.getElement()).thenReturn(element);
    }

    @Test
    public void testMaximize() {
        workbenchLayout.maximize(widget);

        verify(widget).addStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);

        verify(((RequiresResize) widget),
                never()).onResize();
    }

    @Test
    public void testExpandAnimation() {
        final WorkbenchLayoutImpl.ExpandAnimation expandAnimation = new WorkbenchLayoutImpl.ExpandAnimation(widget,
                Maps.<Widget, WorkbenchLayoutImpl.OriginalStyleInfo> newHashMap(),
                mock(SimpleLayoutPanel.class),
                null);

        expandAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
    }

    @Test
    public void testExpandAnimationWithCallback() {
        final Command callback = mock(Command.class);
        final WorkbenchLayoutImpl.ExpandAnimation expandAnimation = new WorkbenchLayoutImpl.ExpandAnimation(widget,
                Maps.<Widget, WorkbenchLayoutImpl.OriginalStyleInfo> newHashMap(),
                mock(SimpleLayoutPanel.class),
                callback);

        expandAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
        verify(callback).execute();
    }

    @Test
    public void testUnMaximize() {
        workbenchLayout.maximize(widget);
        workbenchLayout.unmaximize(widget);

        verify(widget).addStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);
        verify(widget).removeStyleName(WorkbenchLayoutImpl.UF_MAXIMIZED_PANEL);

        verify(((RequiresResize) widget),
                never()).onResize();
    }

    @Test
    public void testCollapseAnimation() {
        final HashMap<Widget, WorkbenchLayoutImpl.OriginalStyleInfo> maximizedWidgetOriginalStyles = Maps.newHashMap();
        maximizedWidgetOriginalStyles.put(widget,
                new WorkbenchLayoutImpl.OriginalStyleInfo(widget));
        final WorkbenchLayoutImpl.CollapseAnimation collapseAnimation = new WorkbenchLayoutImpl.CollapseAnimation(
                widget,
                maximizedWidgetOriginalStyles,
                null);

        collapseAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
    }

    @Test
    public void testCollapseAnimationWithCallback() {
        final HashMap<Widget, WorkbenchLayoutImpl.OriginalStyleInfo> maximizedWidgetOriginalStyles = Maps.newHashMap();
        maximizedWidgetOriginalStyles.put(widget,
                new WorkbenchLayoutImpl.OriginalStyleInfo(widget));
        final Command callback = mock(Command.class);
        final WorkbenchLayoutImpl.CollapseAnimation collapseAnimation = new WorkbenchLayoutImpl.CollapseAnimation(
                widget,
                maximizedWidgetOriginalStyles,
                callback);

        collapseAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
        verify(callback).execute();
    }

    @Test
    public void addWorkbenchProfileCssClass() {

        when(root.getStyleName()).thenReturn("current stylename");

        workbenchLayout.addWorkbenchProfileCssClass(new WorkbenchProfileCssClass("dora"));

        verify(root).removeStyleName("current stylename");
        verify(root).addStyleName(WorkbenchLayoutImpl.UF_ROOT_CSS_CLASS);
        verify(root).addStyleName("dora");

    }
}
