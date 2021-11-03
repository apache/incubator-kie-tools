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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EditHTMLPresenterTest {

    public static final String DEFAULT_CONTENT = "default content";

    public static final String SOME_CONTENT = "some content";

    private ModalConfigurationContext modalConfigurationContext;

    private HtmlEditorPresenter htmlEditor;

    private EditHTMLPresenter.View view;

    private EditHTMLPresenter presenter;

    @Before
    public void setup() {
        modalConfigurationContext = mock(ModalConfigurationContext.class);
        htmlEditor = mock(HtmlEditorPresenter.class);
        view = mock(EditHTMLPresenter.View.class);
        doReturn(DEFAULT_CONTENT).when(view).getHtmlEditorPlaceHolder();
        presenter = spy(new EditHTMLPresenter(view,
                                              htmlEditor));
    }

    @Test
    public void setupEmptyHTMLEditorTest() {
        presenter.init(modalConfigurationContext);

        verify(presenter).setupHTMLEditor();
        verify(modalConfigurationContext).getComponentProperty(HTMLLayoutDragComponent.HTML_CODE_PARAMETER);
        verify(view).getHtmlEditorPlaceHolder();
        verify(htmlEditor).setContent(DEFAULT_CONTENT);
        verify(htmlEditor).load();
    }

    @Test
    public void setupNotEmptyHTMLEditorTest() {
        doReturn(SOME_CONTENT).when(modalConfigurationContext).getComponentProperty(anyString());
        presenter.init(modalConfigurationContext);

        verify(presenter).setupHTMLEditor();
        verify(modalConfigurationContext).getComponentProperty(HTMLLayoutDragComponent.HTML_CODE_PARAMETER);
        verify(view,
               never()).getHtmlEditorPlaceHolder();
        verify(htmlEditor).setContent(SOME_CONTENT);
        verify(htmlEditor).load();
    }

    @Test
    public void showTest() {
        presenter.init(modalConfigurationContext);
        presenter.show();

        verify(view).show();
    }

    @Test
    public void okClickTest() {
        presenter.init(modalConfigurationContext);
        presenter.okClick();

        verify(view).hide();
        verify(presenter).destroyHtmlEditor();
        verify(modalConfigurationContext,
               never()).configurationCancelled();
        verify(modalConfigurationContext).configurationFinished();
    }

    @Test
    public void cancelButtonClickHandlerTest() {
        presenter.init(modalConfigurationContext);
        presenter.cancelClick();

        verify(view).hide();
        verify(presenter).destroyHtmlEditor();
        verify(modalConfigurationContext).configurationCancelled();
        verify(modalConfigurationContext,
               never()).configurationFinished();
    }

    @Test
    public void closeButtonClickHandlerTest() {
        presenter.init(modalConfigurationContext);
        presenter.closeClick();

        verify(presenter.getModalConfigurationContext()).configurationCancelled();
        verify(presenter.getModalConfigurationContext(),
               never()).configurationFinished();
    }
}
