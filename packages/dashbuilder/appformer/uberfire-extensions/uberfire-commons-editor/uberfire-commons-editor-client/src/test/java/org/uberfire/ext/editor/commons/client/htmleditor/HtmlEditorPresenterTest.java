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

package org.uberfire.ext.editor.commons.client.htmleditor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HtmlEditorPresenterTest {

    private HtmlEditorPresenter.View view;

    private HtmlEditorPresenter presenter;

    @Before
    public void setup() {
        view = mock(HtmlEditorPresenter.View.class);
        doReturn("content").when(view).getContent();
        doNothing().when(view).setContent(anyString());
        doNothing().when(view).load();

        presenter = new HtmlEditorPresenter(view);
    }

    @Test
    public void loadTest() {
        presenter.load();

        verify(view).load();
    }

    @Test
    public void getContentTest() {
        String content = presenter.getContent();

        assertEquals("content",
                     content);
        verify(view).getContent();
    }

    @Test
    public void setContentTest() {
        String content = "content";
        presenter.setContent(content);

        verify(view).setContent(content);
    }
}
