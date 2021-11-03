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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class HtmlEditorViewTest {

    private HtmlEditorLibraryLoader libraryLoader;

    private HtmlEditorPresenter presenter;

    private HtmlEditorView view;

    @Before
    public void setup() {
        final TranslationService translationService = mock(TranslationService.class);

        libraryLoader = mock(HtmlEditorLibraryLoader.class);
        view = spy(new HtmlEditorView(translationService,
                                      libraryLoader));
        doNothing().when(view).configureScreenComponents(anyString(),
                                                         anyString());
        view.htmlEditor = mock(Div.class);
        doReturn("content").when(view.htmlEditor).getInnerHTML();
        presenter = spy(new HtmlEditorPresenter(view));

        doNothing().when(view).loadEditor(anyString(),
                                          anyString());
    }

    @Test
    public void editorIsNotLoadedTwice() {
        presenter.load();
        presenter.load();

        verify(view,
               times(1)).loadEditor(anyString(),
                                    anyString());
    }

    @Test
    public void synchronizeViewWhenReturningContent() {
        presenter.getContent();

        verify(view,
               times(1)).synchronizeView();
    }
}
