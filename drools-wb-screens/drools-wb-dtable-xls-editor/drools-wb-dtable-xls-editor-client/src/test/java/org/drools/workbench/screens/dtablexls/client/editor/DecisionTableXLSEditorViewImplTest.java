/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableXLSEditorViewImplTest {

    private static final String SERVLET_URL = "dtablexls/file?clientId=123";

    @Spy
    @InjectMocks
    private DecisionTableXLSEditorViewImpl view;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private AttachmentFileWidget attachmentFileWidget;

    @Mock
    private ClientResourceType type;

    @Mock
    private DecisionTableXLSEditorPresenter presenter;

    @Captor
    private ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Before
    public void setup() {
        doReturn("123").when(view).getClientId();
        doReturn(attachmentFileWidget).when(view).constructUploadWidget(any());
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
                                            commandCaptor.capture(),
                                            any(Command.class));
        commandCaptor.getValue().execute();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(presenter).onUploadSuccess();
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