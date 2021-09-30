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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.events.WorkbenchProfileCssClass;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DockLayoutPanel.class)
public class WorkbenchLayoutImplTest {

    @Mock
    UberfireDocksContainer uberfireDocksContainer;
    @Mock
    WorkbenchPickupDragController dragController;
    private WorkbenchLayoutImpl workbenchLayout;
    private Widget widget;
    @Mock
    private SyncBeanManager iocManager;
    @Mock
    private HeaderPanel root;
    @Mock
    private Div headerPanel;
    @Mock
    private Div footerPanel;
    @Mock
    private WorkbenchDragAndDropManager dndManager;
    private Header header1;

    private Header header2;

    private Footer footer1;

    private Footer footer2;

    @Before
    public void setup() {
        workbenchLayout = new WorkbenchLayoutImpl(iocManager,
                                                  root,
                                                  dndManager,
                                                  uberfireDocksContainer,
                                                  dragController,
                                                  headerPanel,
                                                  footerPanel) {
            @Override
            ElementWrapperWidget<?> createWidgetFrom(HTMLElement h) {
                return mock(ElementWrapperWidget.class);
            }
        };
        widget = mock(Widget.class,
                      withSettings().extraInterfaces(RequiresResize.class));
        final Element element = mock(Element.class);
        when(element.getStyle()).thenReturn(mock(Style.class));
        when(widget.getElement()).thenReturn(element);

        header1 = mock(Header.class);
        header2 = mock(Header.class);

        footer1 = mock(Footer.class);
        footer2 = mock(Footer.class);
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
                                                                                                            Maps.<Widget, WorkbenchLayoutImpl.OriginalStyleInfo>newHashMap(),
                                                                                                            mock(SimpleLayoutPanel.class),
                                                                                                            null);

        expandAnimation.onComplete();

        verify(((RequiresResize) widget)).onResize();
    }

    @Test
    public void testExpandAnimationWithCallback() {
        final Command callback = mock(Command.class);
        final WorkbenchLayoutImpl.ExpandAnimation expandAnimation = new WorkbenchLayoutImpl.ExpandAnimation(widget,
                                                                                                            Maps.<Widget, WorkbenchLayoutImpl.OriginalStyleInfo>newHashMap(),
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
    public void setEmptyHeaderContentsTest() {

        Div headerPanel = workbenchLayout.getHeaderPanel();

        workbenchLayout.setHeaderContents(new ArrayList<>());

        verify(headerPanel,
               never()).appendChild(any());
        verify(root,
               never()).setHeaderWidget(any());
    }

    @Test
    public void setHeaderContentsTest() {

        Div headerPanel = workbenchLayout.getHeaderPanel();

        List<Header> headers = Arrays.asList(header1,
                                             header2);

        workbenchLayout.setHeaderContents(headers);

        verify(headerPanel,
               times(2)).appendChild(any());
        verify(root).setHeaderWidget(any());
    }

    @Test
    public void setEmptyFooterContentsTest() {

        Div footerPanel = workbenchLayout.getFooterPanel();

        workbenchLayout.setFooterContents(new ArrayList<>());

        verify(footerPanel,
               never()).appendChild(any());
        verify(root,
               never()).setFooterWidget(any());
    }

    @Test
    public void setFooterContentsTest() {

        Div footerPanel = workbenchLayout.getFooterPanel();

        List<Footer> footers = Arrays.asList(footer1,
                                             footer2);

        workbenchLayout.setFooterContents(footers);

        verify(footerPanel,
               times(2)).appendChild(any());
        verify(root).setFooterWidget(any());
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