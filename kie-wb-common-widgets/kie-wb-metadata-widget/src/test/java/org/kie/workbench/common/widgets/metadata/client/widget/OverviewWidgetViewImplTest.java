/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.metadata.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class OverviewWidgetViewImplTest {

    @Test
    public void showVersionHistory() {
        final OverviewWidgetViewImpl overviewWidgetView = new OverviewWidgetViewImpl();
        final NavTabs navTabs = mock(NavTabs.class);
        final TabListItem tabListItem = mock(TabListItem.class);
        doReturn(tabListItem).when(navTabs).getWidget(0);
        overviewWidgetView.navTabs = navTabs;

        overviewWidgetView.showVersionHistory();

        verify(tabListItem).showTab();
    }
}