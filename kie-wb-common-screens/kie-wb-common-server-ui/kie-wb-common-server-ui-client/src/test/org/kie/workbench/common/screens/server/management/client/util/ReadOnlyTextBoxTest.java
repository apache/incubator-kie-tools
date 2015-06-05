package org.kie.workbench.common.screens.server.management.client.util;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
        ReadOnlyTextBox readOnlySpy = spy( readOnlyTextBox );

        KeyPressHandler onKeyPressCancelHandler = readOnlySpy.onKeyPressCancelHandler;

        KeyPressEvent keyPressEvent = mock( KeyPressEvent.class );
        when( keyPressEvent.getSource() ).thenReturn( readOnlySpy );

        onKeyPressCancelHandler.onKeyPress( keyPressEvent );

        verify( readOnlySpy ).cancelKey();

    }

}