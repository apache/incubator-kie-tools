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

package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TechnicalViewWidgetTest {

    @GwtMock
    Explorer explorer;

    private TechnicalViewWidget technicalViewWidget;

    @Before
    public void setUp() throws Exception {
        technicalViewWidget = new TechnicalViewWidget() {
            {
                explorer = TechnicalViewWidgetTest.this.explorer;
            }
        };
    }

    @Test
    public void testInit() throws Exception {
        final BaseViewPresenter presenter = mock(BaseViewPresenter.class);
        technicalViewWidget.init(presenter);

        verify(explorer).init(
                any(NavigatorOptions.class),
                eq(Explorer.NavType.BREADCRUMB),
                eq(presenter));
    }

    @Test
    public void showHeaderNavigation() throws Exception {
        technicalViewWidget.showHeaderNavigator();

        verify(explorer).showHeaderNavigator();
        verify(explorer,
               never()).hideHeaderNavigator();
    }

    @Test
    public void hideHeaderNavigation() throws Exception {
        technicalViewWidget.hideHeaderNavigator();

        verify(explorer).hideHeaderNavigator();
        verify(explorer,
               never()).showHeaderNavigator();
    }
}