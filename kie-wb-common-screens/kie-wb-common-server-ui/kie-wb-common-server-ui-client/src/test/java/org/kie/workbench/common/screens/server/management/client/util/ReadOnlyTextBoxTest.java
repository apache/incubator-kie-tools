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

package org.kie.workbench.common.screens.server.management.client.util;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ReadOnlyTextBoxTest {

    private ReadOnlyTextBox readOnlyTextBox;

    @Test
    public void testOnPastePreventBrowserDefault() throws Exception {
        readOnlyTextBox = new ReadOnlyTextBox() {
            @Override
            int getType( Event event ) {
                return Event.ONPASTE;
            }
        };

        Event pasteEvent = mock( Event.class );
        readOnlyTextBox.onBrowserEvent( pasteEvent );
        verify( pasteEvent ).preventDefault();
    }

    @Test
    public void testCancelOnKeyPress() throws Exception {
        readOnlyTextBox = new ReadOnlyTextBox();
        final ReadOnlyTextBox readOnlySpy = spy( readOnlyTextBox );

        final KeyPressEvent keyPressEvent = mock( KeyPressEvent.class );
        when( keyPressEvent.getSource() ).thenReturn( readOnlySpy );

        readOnlySpy.onKeyPressCancelHandler.onKeyPress( keyPressEvent );

        verify( readOnlySpy ).cancelKey();
    }
}