package org.kie.workbench.common.screens.server.management.client.util;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class ReadOnlyTextBox extends TextBox {

    final KeyPressHandler onKeyPressCancelHandler = new KeyPressHandler() {
        public void onKeyPress( KeyPressEvent event ) {
            ( (TextBox) event.getSource() ).cancelKey();
        }
    };

    public ReadOnlyTextBox() {
        super();
        sinkEvents( Event.ONPASTE );
        addKeyPressHandler( onKeyPressCancelHandler );
    }

    @Override
    public void onBrowserEvent( Event event ) {
        super.onBrowserEvent( event );

        switch ( getType( event ) ) {
            case Event.ONPASTE:
                event.preventDefault();
                break;
        }
    }

    int getType( Event event ) {
        return DOM.eventGetType( event );
    }

}
