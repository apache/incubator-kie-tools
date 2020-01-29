/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditorView;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KieEditorWrapperViewTest {

    @Mock
    private MultiPageEditorView view;

    @Spy
    private KieEditorWrapperViewImpl kieView;

    @Before
    public void mockComponents() throws Exception {
        doReturn(view).when(kieView).getView();
        doNothing().when(kieView).selectPage(anyInt());
    }

    @Test
    public void testSelectOverviewTab() {
        doReturn(123).when(view).getPageIndex(CommonConstants.INSTANCE.Overview());

        kieView.selectOverviewTab();

        verify(kieView).setSelectedTab(eq(123));
    }

    @Test
    public void testOverviewTabIsSelected() {
        doReturn(123).when(view).getPageIndex(CommonConstants.INSTANCE.Overview());
        doReturn(123).when(kieView).selectedPage();

        assertTrue("Indexes are same", kieView.isOverviewTabSelected());
    }

    @Test
    public void testOverviewTabIsNotSelected() {
        doReturn(123).when(view).getPageIndex(CommonConstants.INSTANCE.Overview());
        doReturn(321).when(kieView).selectedPage();

        assertFalse("Indexes are different", kieView.isOverviewTabSelected());
    }
}
