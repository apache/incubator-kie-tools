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

package org.uberfire.client.views.pfly.multipage;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TabPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.tab.TabPanelEntry;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.client.workbench.widgets.multipage.PageView;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiPageEditorViewImplTest {

    @GwtMock
    TabPanelEntry.DropDownTabListItem dropDownTabListItem;

    @GwtMock
    TabPane contentPane;

    MultiPageEditorViewImpl multiPageEditorView;

    @Before
    public void setup() {
        multiPageEditorView = new MultiPageEditorViewImpl();
        multiPageEditorView.init();
    }

    @Test
    public void testAddPage() {
        final Page page = mock(Page.class);
        when(page.getLabel()).thenReturn("label1", "label2");
        when(page.getView()).thenReturn(mock(PageView.class));
        when(contentPane.isActive()).thenReturn(false, true);

        multiPageEditorView.addPage(page);

        multiPageEditorView.addPage(page);

        verify(dropDownTabListItem).showTab(false);
        verify(dropDownTabListItem).setActive(true);
    }

}