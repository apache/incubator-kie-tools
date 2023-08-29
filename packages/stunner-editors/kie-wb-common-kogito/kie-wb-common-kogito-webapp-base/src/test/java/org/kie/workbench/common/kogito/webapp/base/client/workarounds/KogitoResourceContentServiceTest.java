/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.kogito.webapp.base.client.workarounds;

import java.util.List;

import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.UUID;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KogitoResourceContentServiceTest {

    private static final String FILE_NAME = "FILE_NAME";
    private static final String ALL_PATTERN = "*";
    private static final String DMN_PATTERN = "*.dmn";
    private static final String NULL_PATTERN = null;
    private static final String UNKNOWN_FILE = "UNKNOWN_FILE";
    private static final String FILE_CONTENT = "FILE_CONTENT";
    private static final Object REJECT_OBJECT = "REJECT_OBJECT";

    @Mock
    private ResourceContentService resourceContentServiceMock;

    private String[] files;
    private String[] dmnFiles;

    private KogitoResourceContentService kogitoResourceContentService;

    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        files = new String[6];
        for (int i = 0; i < 6; i++) {
            String suffix = i < 3 ? "scesim" : "dmn";
            files[i] = getFileUriMock(suffix);
        }
        dmnFiles = new String[3];
        System.arraycopy(files, 3, dmnFiles, 0, 3);
        doReturn(promises.resolve(FILE_CONTENT)).when(resourceContentServiceMock).get(FILE_NAME);
        doReturn(promises.reject(REJECT_OBJECT)).when(resourceContentServiceMock).get(UNKNOWN_FILE);
        doReturn(promises.resolve(files)).when(resourceContentServiceMock).list(ALL_PATTERN);
        doReturn(promises.resolve(dmnFiles)).when(resourceContentServiceMock).list(DMN_PATTERN);
        doReturn(promises.reject(REJECT_OBJECT)).when(resourceContentServiceMock).list(NULL_PATTERN);
        kogitoResourceContentService = new KogitoResourceContentService(resourceContentServiceMock, new SyncPromises());
    }

    @Test
    public void loadFileNoException() {
        RemoteCallback<String> testingCallbackMock = mock(RemoteCallback.class);
        kogitoResourceContentService.loadFile(FILE_NAME, testingCallbackMock, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).get(eq(FILE_NAME));
        verify(testingCallbackMock, times(1)).callback(eq(FILE_CONTENT));
    }

    @Test
    public void loadFileException() {
        ErrorCallback<Object> testingCallbackSpy = spy(new ErrorCallback<Object>() {
            @Override
            public boolean error(Object message, Throwable throwable) {
                assertEquals("Error " + REJECT_OBJECT, message);
                assertEquals("Failed to load file "+ UNKNOWN_FILE, throwable.getMessage());
                return false;
            }
        });
        kogitoResourceContentService.loadFile(UNKNOWN_FILE, mock(RemoteCallback.class), testingCallbackSpy);
        verify(resourceContentServiceMock, times(1)).get(eq(UNKNOWN_FILE));
        verify(testingCallbackSpy, times(1)).error(isA(String.class), isA(Throwable.class));

    }

    @Test
    public void getAllItems() {
        RemoteCallback<List<String>> testingCallbackSpy = spy(new RemoteCallback<List<String>>() {
            @Override
            public void callback(List<String> response) {
                assertEquals(files.length, response.size());
            }
        });
        kogitoResourceContentService.getAllItems(testingCallbackSpy, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).list(eq(ALL_PATTERN));
        verify(testingCallbackSpy, times(1)).callback(isA(List.class));
    }

    @Test
    public void getFilteredItemsNoException() {
        RemoteCallback<List<String>> testingCallbackSpy = spy(new RemoteCallback<List<String>>() {
            @Override
            public void callback(List<String> response) {
                assertEquals(dmnFiles.length, response.size());
                response.forEach(fileName -> assertEquals("dmn", fileName.substring(fileName.lastIndexOf('.') + 1)));
            }
        });
        kogitoResourceContentService.getFilteredItems(DMN_PATTERN, testingCallbackSpy, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).list(eq(DMN_PATTERN));
        verify(testingCallbackSpy, times(1)).callback(isA(List.class));
    }

    @Test
    public void getFilteredItemsException() {
        ErrorCallback<Object> testingCallbackSpy = spy(new ErrorCallback<Object>() {
            @Override
            public boolean error(Object message, Throwable throwable) {
                assertEquals("Error " + REJECT_OBJECT, message);
                assertEquals("Failed to retrieve files with pattern "+ NULL_PATTERN, throwable.getMessage());
                return false;
            }
        });
        kogitoResourceContentService.getFilteredItems(NULL_PATTERN, mock(RemoteCallback.class), testingCallbackSpy);
        verify(resourceContentServiceMock, times(1)).list(eq(NULL_PATTERN));
        verify(testingCallbackSpy, times(1)).error(isA(String.class), isA(Throwable.class));
    }

    private String getFileUriMock(String suffix) {
        return UUID.uuid() + "." + suffix;
    }
}