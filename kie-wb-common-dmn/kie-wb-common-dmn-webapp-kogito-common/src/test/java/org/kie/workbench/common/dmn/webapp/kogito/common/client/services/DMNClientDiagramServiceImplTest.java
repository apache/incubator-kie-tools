/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNClientDiagramServiceImplTest {

    private DMNClientDiagramServiceImpl service;

    @Before
    public void setup() {
        service = spy(new DMNClientDiagramServiceImpl());
    }

    @Test
    public void testTransformWhenFileIsNew() {
        final String fileName = "file.dmn";
        final String xml = "";
        final ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        final String title = "file";

        doNothing().when(service).doNewDiagram(title, callback);

        service.transform(fileName, xml, callback);

        verify(service).doNewDiagram(title, callback);
    }

    @Test
    public void testTransformWhenFileIsNotNew() {
        final String fileName = "file.dmn";
        final String xml = "xml";
        final ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        final String title = "title";

        doNothing().when(service).doNewDiagram(title, callback);

        service.transform(fileName, xml, callback);

        verify(service, never()).doNewDiagram(title, callback);
        verify(service).doTransformation(xml, callback);
    }

    @Test
    public void testGetDiagramTitleWhenIsEmpty() {
        final String expected = "some uuid";
        doReturn(expected).when(service).generateDiagramTitle();
        final String actual = service.getDiagramTitle("");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsFileName() {
        final String fileName = "file.dmn";
        final String expected = "file";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsWindowsPath() {
        final String fileName = "C:\\my path\\folder\\file.dmn";
        final String expected = "file";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsUnixPath() {
        final String fileName = "/users/user/file.dmn";
        final String expected = "file";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsWindowsPathMoreWords() {
        final String fileName = "C:\\my path\\folder\\file a.dmn";
        final String expected = "file a";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsUnixPathMoreWords() {
        final String fileName = "/users/user/file a.dmn";
        final String expected = "file a";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenMoreDotsContained() {
        final String fileName = "/users/user/file.template.dmn";
        final String expected = "file.template";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsNotFileNameOrEmpty() {
        final String fileName = "Something";
        final String expected = "Something";

        final String actual = service.getDiagramTitle(fileName);

        assertEquals(expected, actual);
    }
}