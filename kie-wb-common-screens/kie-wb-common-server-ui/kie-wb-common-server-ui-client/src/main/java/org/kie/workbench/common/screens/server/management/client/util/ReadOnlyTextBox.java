package org.kie.workbench.common.screens.server.management.client.util;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class ReadOnlyTextBox extends TextBox {

    private final KeyPressHandler readOnlyTextBox = new KeyPressHandler() {
        public void onKeyPress( KeyPressEvent event ) {
            ( (TextBox) event.getSource() ).cancelKey();
        }
    };

    public ReadOnlyTextBox() {
        super();
        sinkEvents( Event.ONPASTE );
        addKeyPressHandler( readOnlyTextBox );
    }

    @Override
    public void onBrowserEvent( Event event ) {
        super.onBrowserEvent( event );

        switch ( DOM.eventGetType( event ) ) {
            case Event.ONPASTE:
                event.preventDefault();
                break;
        }
    }

}
