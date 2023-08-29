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


package org.uberfire.client.docks.view;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.workbench.WorkbenchEntryPoint;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocksBars_OpenDockTest {

    @Mock
    private UberfireDocksContainer uberfireDocksContainer;

    @Mock
    private WorkbenchEntryPoint workbenchEntryPoint;

    @InjectMocks
    private DocksBars docksBars;

    @Captor
    private ArgumentCaptor<PlaceRequest> placeRequestArgumentCaptor;

    @Test
    public void testOpenDockParametersArePreserved() {

        final DefaultPlaceRequest myPlace = new DefaultPlaceRequest("myPlace");

        myPlace.addParameter("my_parameter_key",
                             "my_parameter_value");

        docksBars.openDock(getTargetDock(myPlace),
                           getDocksBar());

        verify(workbenchEntryPoint).openDock(placeRequestArgumentCaptor.capture(),
                                             Mockito.<HasWidgets>any());

        final PlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals("myPlace",
                     placeRequest.getIdentifier());
        assertEquals("my_parameter_value",
                     placeRequest.getParameter("my_parameter_key",
                                               ""));
    }

    private UberfireDock getTargetDock(final DefaultPlaceRequest placeRequest) {
        final UberfireDock targetDock = mock(UberfireDock.class);
        when(targetDock.getPlaceRequest()).thenReturn(placeRequest);
        return targetDock;
    }

    private DocksBar getDocksBar() {
        final DocksBar docksBar = mock(DocksBar.class);
        when(docksBar.getCollapsedBar()).thenReturn(mock(DocksCollapsedBar.class));
        final DocksExpandedBar docksExpandedBar = mock(DocksExpandedBar.class);
        when(docksExpandedBar.targetPanel()).thenReturn(mock(FlowPanel.class));
        when(docksBar.getExpandedBar()).thenReturn(docksExpandedBar);
        when(docksBar.getExpandedBar()).thenReturn(mock(DocksExpandedBar.class));
        return docksBar;
    }
}