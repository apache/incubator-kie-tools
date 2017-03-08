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

package org.uberfire.ext.plugin.backend;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginMediaServletTest {

    @Mock
    FileSystem fileSystem;

    @Mock
    Iterable<Path> iterable;

    @Mock
    Iterator<Path> iterator;

    @Mock
    ServletConfig config;

    @Mock
    IOService ioService;

    @Mock
    MediaServletURI mediaServletURI;

    @InjectMocks
    PluginMediaServlet servlet = fakeServlet();

    @Test
    public void testInit() throws Exception {
        when(iterable.iterator()).thenReturn(iterator);
        when(fileSystem.getRootDirectories()).thenReturn(iterable);
        when(config.getInitParameter(anyString())).thenReturn("/fake");
        when(ioService.newFileSystem(any(URI.class),
                                     anyMapOf(String.class,
                                              Class.class))).thenReturn(fileSystem);

        servlet.init(config);

        verify(mediaServletURI).setURI(eq("fake/"));
    }

    @Test
    public void testDoPost() throws Exception {
        final FileItem fileItem = fileMock("C:\\Users\\user\\Desktop\\image.jpg");
        final PluginMediaServlet servlet = spy(fakeServlet(fileItem));

        final HttpServletRequest request = requestMock("/plugins/screen");
        final HttpServletResponse response = responseMock();

        servlet.doPost(request,
                       response);

        verify(servlet).resolve("/screen/media/image.jpg");
    }

    private HttpServletResponse responseMock() {
        return mock(HttpServletResponse.class);
    }

    private HttpServletRequest requestMock(final String requestURI) {
        final HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getContextPath()).thenReturn("");

        return request;
    }

    private FileItem fileMock(final String fileName) {
        final FileItem fileItem = mock(FileItem.class);

        when(fileItem.getName()).thenReturn(fileName);

        return fileItem;
    }

    private PluginMediaServlet fakeServlet() {
        return fakeServlet(null);
    }

    private PluginMediaServlet fakeServlet(final FileItem fileItem) {
        return new PluginMediaServlet() {

            @Override
            protected FileItem getFileItem(HttpServletRequest request) throws FileUploadException {

                return fileItem;
            }

            @Override
            protected void writeResponse(HttpServletResponse response,
                                         String ok) throws IOException {
            }
        };
    }
}
