/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.registry;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ServerRegistryEndpointViewTest {

    private ServerRegistryEndpointView view;

    @Mock
    private ServerRegistryEndpointPresenter presenter;

    @Before
    public void setup() {
        view = new ServerRegistryEndpointView();
        view.init( presenter );
    }

    @Test
    public void testIdTest() {
        when( view.idTextBox.getText() ).thenReturn( "" );
        view.onConnectClick( null );

        verify( view.idGroup, times( 1 ) ).setValidationState( ValidationState.ERROR );

        view.idTextBoxKeyPressHandler.onKeyPress( keyPress( 'x' ) );

        verify( view.idGroup, times( 1 ) ).setValidationState( ValidationState.NONE );
    }

    @Test
    public void testNameTest() {
        when( view.idTextBox.getText() ).thenReturn( "xxx" );

        when( view.nameTextBox.getText() ).thenReturn( "" );

        view.onConnectClick( null );

        verify( view.nameGroup, times( 1 ) ).setValidationState( ValidationState.ERROR );

        view.nameTextBoxKeyPressHandler.onKeyPress( keyPress( 'x' ) );

        verify( view.nameGroup, times( 1 ) ).setValidationState( ValidationState.NONE );
    }

    private KeyPressEvent keyPress( int key ) {
        KeyPressEvent keyPress = mock( KeyPressEvent.class );
        NativeEvent event = mock( NativeEvent.class );
        when( keyPress.getNativeEvent() ).thenReturn( event );
        when( event.getKeyCode() ).thenReturn( key );

        return keyPress;
    }
}