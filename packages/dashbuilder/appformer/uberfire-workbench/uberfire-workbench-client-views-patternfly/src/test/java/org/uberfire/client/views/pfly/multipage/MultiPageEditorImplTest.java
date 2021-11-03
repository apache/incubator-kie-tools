/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class MultiPageEditorImplTest {

    @Mock
    private MultiPageEditorViewImpl view;

    @Mock
    private EventSourceMock<MultiPageEditorSelectedPageEvent> selectedPageEvent;

    @InjectMocks
    private MultiPageEditorImpl editor = spy(new MultiPageEditorImpl());

    @Test
    public void testInit() {
        editor.init();
        verify(view).enableSelectedPageEvent(selectedPageEvent);
    }

    @Test
    public void testAddPageWithIndex() {

        final Page page = mock(Page.class);
        final int index = 1;

        editor.addPage(index, page);

        verify(view).addPage(index, page);
    }

    @Test
    public void testDisablePage() {

        final int index = 1;

        editor.disablePage(index);

        verify(view).disablePage(index);
    }

    @Test
    public void testEnablePage() {

        final int index = 1;

        editor.enablePage(index);

        verify(view).enablePage(index);
    }
}
