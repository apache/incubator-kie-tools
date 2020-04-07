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
package org.drools.workbench.screens.scenariosimulation.webapp.client.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.services.TestingVFSService.CONTENT_PARAMETER_NAME;
import static org.drools.workbench.screens.scenariosimulation.webapp.client.services.TestingVFSService.FILE_NAME_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestingVFSServiceTest {

    private static final String EDITOR_ID = "EDITOR_ID";
    private static final String FILE_NAME = "FILE_NAME";
    private static final String XML = "XML";

    @Mock
    private PlaceManager placeManagerMock;

    @Mock
    private Path pathMock;

    @Mock
    private VFSService vfsServiceMock;

    @Mock
    private RemoteCallback<String> callbackMock;

    @Mock
    private RemoteCallback<List<Path>> itemsCallbackMock;

    @Mock
    private DirectoryStream<Path> directoryStreamMock;

    private List<Path> files;

    private CallerMock<VFSService> vfsServiceCallerMock;

    private TestingVFSService testingVFSService;

    @Before
    public void setup() {
        vfsServiceCallerMock = new CallerMock(vfsServiceMock);
        files = IntStream.range(0, 6).mapToObj(value -> {
            String suffix = value < 3 ? "scesim" : "dmn";
            return getPathMock(suffix);
        }).collect(Collectors.toList());
        when(pathMock.getFileName()).thenReturn(FILE_NAME);
        when(vfsServiceMock.readAllString(eq(pathMock))).thenReturn(XML);
        when(directoryStreamMock.spliterator()).thenReturn(files.spliterator());
        when(vfsServiceMock.newDirectoryStream(eq(pathMock))).thenReturn(directoryStreamMock);
        testingVFSService = new TestingVFSService(placeManagerMock, vfsServiceCallerMock);
    }

    @Test
    public void createDirectory() {
        testingVFSService.createDirectory(pathMock);
        verify(vfsServiceMock, times(1)).createDirectory(eq(pathMock));
    }

    @Test
    public void newFile() {
        ArgumentCaptor<PlaceRequest> placeRequestCaptor = ArgumentCaptor.forClass(PlaceRequest.class);
        testingVFSService.newFile(EDITOR_ID, FILE_NAME);
        verify(placeManagerMock, times(1)).goTo(placeRequestCaptor.capture());
        assertEquals(EDITOR_ID, placeRequestCaptor.getValue().getIdentifier());
        assertEquals(FILE_NAME, placeRequestCaptor.getValue().getParameter(FILE_NAME_PARAMETER_NAME, "WRONG"));
    }

    @Test
    public void openFile() {
        ArgumentCaptor<PlaceRequest> placeRequestCaptor = ArgumentCaptor.forClass(PlaceRequest.class);
        testingVFSService.openFile(pathMock, EDITOR_ID);
        verify(vfsServiceMock, times(1)).readAllString(eq(pathMock));
        verify(placeManagerMock, times(1)).goTo(placeRequestCaptor.capture());
        assertEquals(EDITOR_ID, placeRequestCaptor.getValue().getIdentifier());
        assertEquals(FILE_NAME, placeRequestCaptor.getValue().getParameter(FILE_NAME_PARAMETER_NAME, "WRONG"));
        assertEquals(XML, placeRequestCaptor.getValue().getParameter(CONTENT_PARAMETER_NAME, "WRONG"));
    }

    @Test
    public void loadFile() {
        testingVFSService.loadFile(pathMock, callbackMock, mock(ErrorCallback.class));
        verify(vfsServiceMock, times(1)).readAllString(eq(pathMock));
        verify(callbackMock, times(1)).callback(eq(XML));
    }

    @Test
    public void saveFile() {
        testingVFSService.saveFile(pathMock, XML, callbackMock, mock(ErrorCallback.class));
        verify(vfsServiceMock, times(1)).write(eq(pathMock), eq(XML));
        verify(callbackMock, times(1)).callback(eq(XML));
    }

    @Test
    public void getItemsByPathWithoutSuffix() {
        RemoteCallback<List<Path>> testingCallback = response -> assertEquals(files.size(), response.size());
        testingVFSService.getItemsByPath(pathMock, testingCallback, mock(ErrorCallback.class));
        verify(vfsServiceMock, times(1)).newDirectoryStream(eq(pathMock));
    }

    @Test
    public void getItemsByPathWithSuffix() {
        RemoteCallback<List<Path>> testingCallback = response -> {
            assertTrue(files.size() > response.size());
            response.forEach(path -> assertEquals("dmn", path.getFileName().substring(path.getFileName().lastIndexOf('.') + 1)));
        };
        testingVFSService.getItemsByPath(pathMock, "dmn", testingCallback, mock(ErrorCallback.class));
        verify(vfsServiceMock, times(1)).newDirectoryStream(eq(pathMock));
    }

    private Path getPathMock(String suffix) {
        Path toReturn = mock(Path.class);
        when(toReturn.getFileName()).thenReturn(UUID.uuid() + "." + suffix);
        return toReturn;
    }
}