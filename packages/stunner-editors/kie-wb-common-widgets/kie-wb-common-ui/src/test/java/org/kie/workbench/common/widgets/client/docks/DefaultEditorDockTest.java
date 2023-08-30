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

package org.kie.workbench.common.widgets.client.docks;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEditorDockTest {

    EditorDock dock;

    @Mock
    Instance<EditorDock> docksInstance;

    DefaultEditorDock defaultEditorDock;

    @Before
    public void setUp() throws Exception {
        dock = spy(new EditorDocksMock());
        defaultEditorDock = new DefaultEditorDock(docksInstance);
    }

    @Test
    public void testSatisfied() {
        doReturn(false).when(docksInstance).isUnsatisfied();
        doReturn(dock).when(docksInstance).get();

        defaultEditorDock.hide();
        verify(dock).hide();

        defaultEditorDock.show();
        verify(dock).show();

        assertFalse(dock.isSetup());

        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        defaultEditorDock.setup("identifier", placeRequest);

        verify(dock).setup("identifier", placeRequest);
        assertTrue(dock.isSetup());
    }

    @Test
    public void testUnsatisfied() {
        doReturn(true).when(docksInstance).isUnsatisfied();

        defaultEditorDock.hide();
        defaultEditorDock.show();
        defaultEditorDock.setup("identifier", mock(PlaceRequest.class));

        assertTrue(defaultEditorDock.isSetup());
        verify(docksInstance, never()).get();
    }

    private class EditorDocksMock
            implements EditorDock {

        private boolean isSetup = false;

        @Override
        public boolean isSetup() {
            return isSetup;
        }

        @Override
        public void setup(String identifier, PlaceRequest defaultPlaceRequest) {
            isSetup = true;
        }

        @Override
        public void show() {

        }

        @Override
        public void hide() {

        }
    }
}