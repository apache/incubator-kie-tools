/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.HashSet;

import com.google.gwt.user.client.Window;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActiveOptionsInitTest {

    @GwtMock
    Window window;

    @Mock
    private ExplorerService explorerService;

    private EventSourceMock<ActiveOptionsChangedEvent> changedEvent;
    private ActiveContextOptions options;
    private HashSet<Option> savedOptions;

    @Before
    public void setUp() throws Exception {
        changedEvent = spy( new EventSourceMock<ActiveOptionsChangedEvent>() {
            @Override
            public void fire( ActiveOptionsChangedEvent event ) {

            }
        } );

        savedOptions = new HashSet<Option>();
        when( explorerService.getLastUserOptions() ).thenReturn( savedOptions );

        options = new ActiveContextOptions( new CallerMock<ExplorerService>( explorerService ),
                                            changedEvent );
    }

    @Test
    public void testNoParameters() throws Exception {

        Command completeCommand = mock( Command.class );
        options.init( mock( PlaceRequest.class ),
                      completeCommand );

        assertFalse( options.getOptions().isEmpty() );

        assertTrue( options.isBusinessViewActive() );
        assertTrue( options.isBreadCrumbNavigationVisible() );
        assertFalse( options.areHiddenFilesVisible() );

        verify( completeCommand ).execute();
    }

    @Test
    public void testParametersInPlaceRequest_business_tree() throws Exception {

        Command completeCommand = mock( Command.class );
        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( "mode", "" ) ).thenReturn( "business_tree" );

        options.init( placeRequest,
                      completeCommand );

        assertFalse( options.getOptions().isEmpty() );

        assertTrue( options.isBusinessViewActive() );
        assertTrue( options.isTreeNavigatorVisible() );
        assertFalse( options.areHiddenFilesVisible() );

        verify( completeCommand ).execute();
    }
}
