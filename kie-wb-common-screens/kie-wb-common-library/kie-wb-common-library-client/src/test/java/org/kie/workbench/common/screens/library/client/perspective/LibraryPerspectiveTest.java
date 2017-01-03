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
package org.kie.workbench.common.screens.library.client.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.monitor.LibraryMonitor;
import org.kie.workbench.common.screens.library.client.util.LibraryDocks;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class LibraryPerspectiveTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private LibraryDocks libraryDocks;

    @Mock
    private LibraryMonitor libraryMonitor;

    private LibraryPerspective perspective;

    @Before
    public void setup() {
        perspective = new LibraryPerspective( placeManager, uberfireDocks, libraryDocks, libraryMonitor );
    }

    @Test
    public void testSetupDocks() throws Exception {
        perspective.setupDocks();
        verify( libraryDocks ).start();
    }

    @Test
    public void emptyLibraryScreenIsOpenedWhenThereIsNoProjectAccessibleTest() {
        doReturn( false ).when( libraryMonitor ).thereIsAtLeastOneProjectAccessible();

        final ConditionalPlaceRequest placeRequest = (ConditionalPlaceRequest) perspective.getLibraryPlaceRequest();

        assertEquals( LibraryPlaces.EMPTY_LIBRARY_SCREEN, placeRequest.resolveConditionalPlaceRequest().getIdentifier() );
    }

    @Test
    public void libraryScreenIsOpenedWhenThereIsAtLeastOneProjectAccessibleTest() {
        doReturn( true ).when( libraryMonitor ).thereIsAtLeastOneProjectAccessible();

        final ConditionalPlaceRequest placeRequest = (ConditionalPlaceRequest) perspective.getLibraryPlaceRequest();

        assertEquals( LibraryPlaces.LIBRARY_SCREEN, placeRequest.resolveConditionalPlaceRequest().getIdentifier() );
    }
}