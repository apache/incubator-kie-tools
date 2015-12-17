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

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.Window;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ActiveOptionsInitParamTest {

    private final String mode;
    private final Option option1;
    private final Option option2;

    @GwtMock
    private Window window;

    private EventSourceMock<ActiveOptionsChangedEvent> changedEvent;
    private ActiveContextOptions options;

    public ActiveOptionsInitParamTest( final String mode,
                                       final Option option1,
                                       final Option option2 ) {
        this.mode = mode;
        this.option1 = option1;
        this.option2 = option2;
    }

    @Before
    public void setUp() throws Exception {

        GwtMockito.initMocks( this );

        changedEvent = spy( new EventSourceMock<ActiveOptionsChangedEvent>() {
            @Override
            public void fire( ActiveOptionsChangedEvent event ) {

            }
        } );

    }

    @Test
    public void testParametersInPlaceRequest() throws Exception {

        options = new ActiveContextOptions( new CallerMock<ExplorerService>( mock( ExplorerService.class ) ),
                                            changedEvent );

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( "mode", "" ) ).thenReturn( mode );

        options.init( placeRequest,
                      mock( Command.class ) );

        assertTrue( options.getOptions().contains( option1 ) );
        assertTrue( options.getOptions().contains( option2 ) );
    }

    @Test
    public void testParametersInURL() throws Exception {
        // Not sure how these are different from place request parameters,
        // I feel they are redundant. Afraid to remove them at this point.

        options = new ActiveContextOptions( new CallerMock<ExplorerService>( mock( ExplorerService.class ) ),
                                            changedEvent ) {
            @Override
            protected String getWindowParameter( String parameterName ) {
                if ( parameterName.equals( "explorer_mode" ) ) {
                    return mode;
                } else {
                    return null;
                }
            }
        };

        options.init( mock( PlaceRequest.class ),
                      mock( Command.class ) );

        assertTrue( options.getOptions().contains( option1 ) );
        assertTrue( options.getOptions().contains( option2 ) );
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                {"business_tree", Option.BUSINESS_CONTENT, Option.TREE_NAVIGATOR},
                {"business_explorer", Option.BUSINESS_CONTENT, Option.BREADCRUMB_NAVIGATOR},
                {"tech_tree", Option.TECHNICAL_CONTENT, Option.TREE_NAVIGATOR},
                {"tech_explorer", Option.TECHNICAL_CONTENT, Option.BREADCRUMB_NAVIGATOR}
        } );
    }

}
