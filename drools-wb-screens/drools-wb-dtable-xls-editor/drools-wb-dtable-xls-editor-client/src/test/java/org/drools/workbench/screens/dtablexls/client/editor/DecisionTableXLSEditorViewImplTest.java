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

package org.drools.workbench.screens.dtablexls.client.editor;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableXLSEditorViewImplTest {

    private static final String SERVLET_URL = "dtablexls/file?clientId=123";
    private DecisionTableXLSEditorViewImpl view;

    @Mock
    AttachmentFileWidget attachmentFileWidget;

    @Mock
    ClientResourceType type;

    @Mock
    DecisionTableXLSEditorPresenter presenter;

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    @Before
    public void setup() {
        view = new DecisionTableXLSEditorViewImpl() {

            @Override
            String getClientId() {
                return "123";
            }

            @Override
            protected AttachmentFileWidget constructUploadWidget(ClientResourceType resourceTypeDefinition) {
                return attachmentFileWidget;
            }
        };

        view.init(presenter);
    }

    @Test
    public void testGetDownloadUrl() throws Exception {
        assertEquals(SERVLET_URL + "&attachmentPath=",
                     view.getDownloadUrl(path()));
    }

    @Test
    public void getServletUrl() throws Exception {
        assertEquals(SERVLET_URL,
                     view.getServletUrl());
    }

    @Test
    public void testUploadWidgetClickHandler() throws Exception {
        doCallRealMethod().when(presenter).onUpload();
        view.setupUploadWidget(type);
        verify(attachmentFileWidget).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);
        verify(presenter).submit();
    }

    @Test
    public void testSubmit() throws Exception {
        Path path = mock(Path.class);
        view.setupUploadWidget(type);
        view.submit(path);
        verify(attachmentFileWidget).submit(eq(path),
                                            eq(SERVLET_URL),
                                            any(Command.class),
                                            any(Command.class));
    }

    private Path path() {
        return new Path() {
            @Override
            public String getFileName() {
                return "";
            }

            @Override
            public String toURI() {
                return "";
            }

            @Override
            public int compareTo(final Path o) {
                return 0;
            }
        };
    }
}