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

package org.kie.workbench.common.screens.datasource.management.client.wizard.driver;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DriverDefPageTest
        extends DriverWizardTestBase {

    @Before
    public void setup() {
        super.setup();
    }

    /**
     * Tests the case where the user completes the page by entering correct values.
     */
    @Test
    public void testValidCompletion() {
        //emulates the user completing the page by typing valid values in all fields.
        completeValidDefPage();

        //modification event should have been fired.
        verify( statusChangeEvent, times( 5 ) ).fire( any( WizardPageStatusChangeEvent.class ) );

        //the DriverDef should have been populated
        assertEquals( NAME, driverDef.getName() );
        assertEquals( GROUP_ID, driverDef.getGroupId() );
        assertEquals( ARTIFACT_ID, driverDef.getArtifactId() );
        assertEquals( VERSION, driverDef.getVersion() );
        assertEquals( DRIVER_CLASS, driverDef.getDriverClass() );

        //the page should be in completed state.
        defPage.isComplete( new Callback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );
    }

    /**
     * Tests the case where the user enters incorrect values and thus the page won't be in completed status.
     */
    @Test
    public void testInvalidCompletion() {
        //first force the page to be in valid state.
        editorHelper.setValid( true );
        //now the page should be in completed state.
        defPage.isComplete( new Callback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );

        //now emulates the entering of a wrong value e.g. for the driver class name
        when( mainPanelView.getDriverClass() ).thenReturn( "SomeWrongClassName" );
        editorHelper.onDriverClassChange();

        //now the page should be in un-completed state.
        defPage.isComplete( new Callback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                assertFalse( result );
            }
        } );
    }
}
