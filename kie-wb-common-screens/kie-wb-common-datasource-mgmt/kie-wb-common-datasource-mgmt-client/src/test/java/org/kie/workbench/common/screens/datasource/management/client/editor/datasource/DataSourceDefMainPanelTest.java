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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSourceDefMainPanelTest
        implements DataSourceManagementTestConstants {

    @Mock
    private DataSourceDefMainPanelView view;

    private DataSourceDefMainPanel mainPanel;

    private String name;

    private String connectionURL;

    private String user;

    private String password;

    private String driver;

    @Before
    public void setup() {
        mainPanel = new DataSourceDefMainPanel( view );
        name = null;
        connectionURL = null;
        user = null;
        password = null;
        driver = null;
        mainPanel.setHandler( new DataSourceDefMainPanelView.Handler() {
            @Override
            public void onNameChange() {
                name = view.getName();
            }

            @Override
            public void onConnectionURLChange() {
                connectionURL = view.getConnectionURL();
            }

            @Override
            public void onUserChange() {
                user = view.getUser();
            }

            @Override
            public void onPasswordChange() {
                password = view.getPassword();
            }

            @Override
            public void onDriverChange() {
                driver = view.getDriver();
            }

            @Override
            public void onTestConnection() {

            }
        } );
    }

    @Test
    public void testSetName() {
        mainPanel.setName( NAME );
        verify( view, times( 1 ) ).setName( NAME );
    }

    @Test
    public void testGetName() {
        when ( view.getName() ).thenReturn( NAME );
        assertEquals( NAME, mainPanel.getName() );
    }

    @Test
    public void testSetNameError() {
        mainPanel.setNameErrorMessage( ERROR );
        verify( view, times( 1 ) ).setNameErrorMessage( ERROR );
    }

    @Test
    public void clearNameError() {
        mainPanel.clearNameErrorMessage();
        verify( view, times( 1 ) ).clearNameErrorMessage();
    }

    @Test
    public void testNameChange() {
        when ( view.getName() ).thenReturn( NAME );
        //emulates the presenter method executed from the UI.
        mainPanel.onNameChange();
        verify( view, times( 1 ) ).getName();
        //the handler should have been invoked and collected the expected value.
        assertEquals( NAME, name );
    }

    @Test
    public void testSetConnectionURL() {
        mainPanel.setConnectionURL( CONNECTION_URL );
        verify( view, times( 1 ) ).setConnectionURL( CONNECTION_URL );
    }

    @Test
    public void testGetConnectionURL() {
        when ( view.getConnectionURL() ).thenReturn( CONNECTION_URL );
        assertEquals( CONNECTION_URL, mainPanel.getConnectionURL() );
    }

    @Test
    public void testSetConnectionURLError() {
        mainPanel.setConnectionURLErrorMessage( ERROR );
        verify( view, times( 1 ) ).setConnectionURLErrorMessage( ERROR );
    }

    @Test
    public void clearConnectionURLError() {
        mainPanel.clearConnectionURLErrorMessage();
        verify( view, times( 1 ) ).clearConnectionURLErrorMessage();
    }

    @Test
    public void testConnectionURLChange() {
        when ( view.getConnectionURL() ).thenReturn( CONNECTION_URL );
        //emulates the presenter method executed from the UI.
        mainPanel.onConnectionURLChange();
        verify( view, times( 1 ) ).getConnectionURL();
        //the handler should have been invoked and collected the expected value.
        assertEquals( CONNECTION_URL, connectionURL );
    }

    @Test
    public void testSetUser() {
        mainPanel.setUser( USER );
        verify( view, times( 1 ) ).setUser( USER );
    }

    @Test
    public void testGetUser() {
        when ( view.getUser() ).thenReturn( USER );
        assertEquals( USER, mainPanel.getUser() );
    }

    @Test
    public void testSetUserError() {
        mainPanel.setUserErrorMessage( ERROR );
        verify( view, times( 1 ) ).setUserErrorMessage( ERROR );
    }

    @Test
    public void clearUserError() {
        mainPanel.clearUserErrorMessage();
        verify( view, times( 1 ) ).clearUserErrorMessage();
    }

    @Test
    public void testUserChange() {
        when ( view.getUser() ).thenReturn( USER );
        //emulates the presenter method executed from the UI.
        mainPanel.onUserChange();
        verify( view, times( 1 ) ).getUser();
        //the handler should have been invoked and collected the expected value.
        assertEquals( USER, user );
    }

    @Test
    public void testSetPassword() {
        mainPanel.setPassword( PASSWORD );
        verify( view, times( 1 ) ).setPassword( PASSWORD );
    }

    @Test
    public void testGetPassword() {
        when ( view.getPassword() ).thenReturn( PASSWORD );
        assertEquals( PASSWORD, mainPanel.getPassword() );
    }

    @Test
    public void testSetPasswordError() {
        mainPanel.setPasswordErrorMessage( ERROR );
        verify( view, times( 1 ) ).setPasswordErrorMessage( ERROR );
    }

    @Test
    public void clearPasswordError() {
        mainPanel.clearPasswordErrorMessage();
        verify( view, times( 1 ) ).clearPasswordErrorMessage();
    }

    @Test
    public void testPasswordChange() {
        when ( view.getPassword() ).thenReturn( PASSWORD );
        //emulates the presenter method executed from the UI.
        mainPanel.onPasswordChange();
        verify( view, times( 1 ) ).getPassword();
        //the handler should have been invoked and collected the expected value.
        assertEquals( PASSWORD, password );
    }

    @Test
    public void testSetDriver() {
        mainPanel.setDriver( DRIVER_UUID );
        verify( view, times( 1 ) ).setDriver( DRIVER_UUID );
    }

    @Test
    public void testGetDriver() {
        when ( view.getDriver() ).thenReturn( DRIVER_UUID );
        assertEquals( DRIVER_UUID, mainPanel.getDriver() );
    }

    @Test
    public void testSetDriverError() {
        mainPanel.setDriverErrorMessage( ERROR );
        verify( view, times( 1 ) ).setDriverErrorMessage( ERROR );
    }

    @Test
    public void clearDriverError() {
        mainPanel.clearDriverErrorMessage();
        verify( view, times( 1 ) ).clearDriverErrorMessage();
    }

    @Test
    public void testDriverChange() {
        when ( view.getDriver() ).thenReturn( DRIVER_UUID );
        //emulates the presenter method executed from the UI.
        mainPanel.onDriverChange();
        verify( view, times( 1 ) ).getDriver();
        //the handler should have been invoked and collected the expected value.
        assertEquals( DRIVER_UUID, driver );
    }

    @Test
    public void testLoadDriverOptions() {
        List<Pair<String, String>> options = new ArrayList<>( );
        mainPanel.loadDriverOptions( options, true );
        verify( view, times( 1 ) ).loadDriverOptions( options, true );
    }

}
