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

package org.kie.workbench.common.screens.explorer.client.widgets.business;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.loading.BusyIndicator;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagSelector;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BusinessViewWidgetTest {

    @GwtMock
    Explorer explorer;

    @GwtMock
    TagSelector tagSelector;

    @GwtMock
    PanelGroup itemsContainer;

    @GwtMock
    BusyIndicator busyIndicator;

    @GwtMock
    BaseViewPresenter presenter;

    private BusinessViewWidget businessViewWidget;

    @Before
    public void setUp() throws Exception {
        businessViewWidget = new BusinessViewWidget() {
            {
                explorer = BusinessViewWidgetTest.this.explorer;
                tagSelector = BusinessViewWidgetTest.this.tagSelector;
                itemsContainer = BusinessViewWidgetTest.this.itemsContainer;
                busyIndicator = BusinessViewWidgetTest.this.busyIndicator;
            }
        };

        businessViewWidget.init(presenter);
    }

    @Test
    public void testInit() throws Exception {
        final BaseViewPresenter presenter = mock(BaseViewPresenter.class);
        businessViewWidget.init(presenter);

        verify(explorer).init(any(NavigatorOptions.class),
                              eq(Explorer.NavType.TREE),
                              eq(presenter));
    }

    @Test
    public void showHeaderNavigation() throws Exception {
        businessViewWidget.showHeaderNavigator();

        verify(explorer).showHeaderNavigator();
        verify(explorer,
               never()).hideHeaderNavigator();
    }

    @Test
    public void hideHeaderNavigation() throws Exception {
        businessViewWidget.hideHeaderNavigator();

        verify(explorer).hideHeaderNavigator();
        verify(explorer,
               never()).showHeaderNavigator();
    }

    @Test
    public void hideContentTest() {
        final String msg = "Loading";

        businessViewWidget.showBusyIndicator(msg);

        verify(busyIndicator).showBusyIndicator(msg);
        verify(explorer).setVisible(false);
        verify(itemsContainer).setVisible(false);
        verify(tagSelector).hide();
    }

    @Test
    public void showContentNoTagsTest() {
        doReturn(false).when(presenter).canShowTags();

        businessViewWidget.hideBusyIndicator();

        verify(busyIndicator).hideBusyIndicator();
        verify(tagSelector).hide();
        verify(explorer).setVisible(true);
        verify(itemsContainer).setVisible(true);
    }

    @Test
    public void showContentWithTagsTest() {
        doReturn(true).when(presenter).canShowTags();

        businessViewWidget.hideBusyIndicator();

        verify(busyIndicator).hideBusyIndicator();
        verify(tagSelector).show();
        verify(explorer).setVisible(true);
        verify(itemsContainer).setVisible(true);
    }
}