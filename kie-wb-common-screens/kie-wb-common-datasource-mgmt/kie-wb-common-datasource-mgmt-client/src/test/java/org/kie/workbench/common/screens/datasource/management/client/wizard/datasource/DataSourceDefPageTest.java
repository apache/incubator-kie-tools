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

package org.kie.workbench.common.screens.datasource.management.client.wizard.datasource;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSourceDefPageTest
        extends DataSourceWizardTestBase {

    @Before
    public void setup() {
        super.setup();
    }

    private void loadPage() {
        //emulates the method invoked by the wizard in order to initialize the page.
        defPage.loadDrivers( new Command() {
            @Override public void execute() {
                defPageLoadedOK[ 0 ] = true;
            }
        }, new ParameterizedCommand<Throwable>() {
            @Override public void execute( Throwable parameter ) {
                defPageLoadedOK[ 0 ] = false;
            }
        } );
    }

    /**
     * Tests the scenario when the wizard is being started and makes the page to be initialized.
     */
    @Test
    public void testPageLoad() {
        loadPage();
        assertTrue( defPageLoadedOK[ 0 ] );
        //checks that the page UI has been properly loaded with the drivers information.
        verify( mainPanelView, times( 1 ) ).loadDriverOptions( eq( options ), eq( true ) );
    }

    /**
     * Tests the case where the user completes the page by entering correct values.
     */
    @Test
    public void testValidCompletion() {

        //emulates the page has already been loaded by the Wizard.
        loadPage();

        //emulates the user completing the page by typing valid values in all fields.
        completeValidDefPage();

        //status change event should have been fired for all the modified fields.
        verify( statusChangeEvent, times( 5 ) ).fire( any( WizardPageStatusChangeEvent.class ) );

        //the DataSourceDef should have been properly populated.
        assertEquals( NAME, dataSourceDef.getName() );
        assertEquals( CONNECTION_URL, dataSourceDef.getConnectionURL() );
        assertEquals( USER, dataSourceDef.getUser() );
        assertEquals( PASSWORD, dataSourceDef.getPassword() );
        assertEquals( DRIVER_UUID, dataSourceDef.getDriverUuid() );

        //the page should be in completed state.
        defPage.isComplete( new Callback<Boolean>() {
            @Override public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );
    }

    /**
     * Test the case where the user enters incorrect values and thus the page won't be in completed status.
     */
    public void testInvalidCompletion() {
        //emulates the page has already been loaded by the Wizard.
        loadPage();

        //force the page to be in valid state
        editorHelper.setValid( true );
        //now the page should be in completed state
        defPage.isComplete( new Callback<Boolean>() {
            @Override public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );

        //now emulates the user entering a wrong value
        when( mainPanelView.getConnectionURL() ).thenReturn( "SomeInvalidConnectionURL" );
        editorHelper.onConnectionURLChange();

        //now the page should be in un-completed state
        defPage.isComplete( new Callback<Boolean>() {
            @Override public void callback( Boolean result ) {
                assertFalse( result );
            }
        } );
    }
}