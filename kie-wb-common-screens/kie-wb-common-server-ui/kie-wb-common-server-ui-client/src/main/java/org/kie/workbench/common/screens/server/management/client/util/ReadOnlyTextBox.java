/*
 * Copyright 2015 JBoss Inc
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

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import org.gwtbootstrap3.client.ui.TextBox;

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
