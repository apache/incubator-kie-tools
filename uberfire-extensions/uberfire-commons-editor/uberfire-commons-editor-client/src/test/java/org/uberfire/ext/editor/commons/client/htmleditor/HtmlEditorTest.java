/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.service.htmleditor.HtmlEditorService;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class HtmlEditorTest {

    @Mock
    private HtmlResourceType htmlResourceType;

    @Mock
    private HtmlEditorPresenter presenter;

    @Mock
    private HtmlEditorService htmlEditorService;

    @Mock
    private VersionRecordManager versionRecordManagerMock;

    @Mock
    private BaseEditorView baseViewMock;

    private Caller<HtmlEditorService> htmlEditorServiceCaller;

    private HtmlEditor htmlEditor;

    @Before
    public void setup() {
        htmlEditorServiceCaller = new CallerMock<>(htmlEditorService);
        htmlEditor = spy(new HtmlEditor(htmlResourceType, presenter, htmlEditorServiceCaller) {{
            baseView = baseViewMock;
            versionRecordManager = versionRecordManagerMock;
        }});
    }

    @Test
    public void testGetContentSupplier() {

        final String content = "content";

        doReturn(content).when(presenter).getContent();

        final Supplier<String> contentSupplier = htmlEditor.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertEquals(htmlEditorServiceCaller, htmlEditor.getSaveAndRenameServiceCaller());
    }

    @Test
    public void testLoadContent() {

        final ObservablePath path = mock(ObservablePath.class);

        doReturn(path).when(versionRecordManagerMock).getCurrentPath();

        htmlEditor.loadContent();

        verify(baseViewMock).hideBusyIndicator();
        verify(htmlEditorService).load(path);
        verify(presenter).setContent(anyString());
    }

    @Test
    public void testSave() {

        final ObservablePath path = mock(ObservablePath.class);
        final RemoteCallback successCallback = mock(RemoteCallback.class);
        final String content = "content";

        doReturn(content).when(presenter).getContent();
        doReturn(path).when(versionRecordManagerMock).getCurrentPath();
        doReturn(successCallback).when(htmlEditor).getSaveSuccessCallback(anyInt());

        htmlEditor.save();

        verify(htmlEditorService).save(path, content, null, null);
        verify(successCallback).callback(any(Path.class));
    }
}
