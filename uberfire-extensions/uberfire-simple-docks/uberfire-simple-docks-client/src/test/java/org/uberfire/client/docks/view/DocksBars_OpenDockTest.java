/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.client.docks.view;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.bars.DocksExpandedBar;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DocksBars_OpenDockTest {

    @Mock
    private UberfireDocksContainer uberfireDocksContainer;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private MenuBuilder menuBuilder;

    @InjectMocks
    private DocksBars docksBars;

    @Captor
    private ArgumentCaptor<PlaceRequest> placeRequestArgumentCaptor;

    @Test
    public void testOpenDockParametersArePreserved() throws Exception {

        final DefaultPlaceRequest myPlace = new DefaultPlaceRequest("myPlace");

        myPlace.addParameter("my_parameter_key",
                             "my_parameter_value");

        docksBars.openDock(getTargetDock(myPlace),
                           getDocksBar());

        verify(placeManager).goTo(placeRequestArgumentCaptor.capture(),
                                  any(FlowPanel.class));

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