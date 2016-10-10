/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyboardEvent;
import org.uberfire.client.mvp.UberView;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class LienzoPanel implements IsWidget {

    public interface View extends UberView<LienzoPanel> {

        void destroy();

    }

    Event<KeyPressEvent> keyPressEvent;
    Event<KeyDownEvent> keyDownEvent;
    Event<KeyUpEvent> keyUpEvent;
    View view;

    private boolean listening;

    @Inject
    public LienzoPanel( final Event<KeyPressEvent> keyPressEvent,
                        final Event<KeyDownEvent> keyDownEvent,
                        final Event<KeyUpEvent> keyUpEvent ) {
        this.keyPressEvent = keyPressEvent;
        this.keyDownEvent = keyDownEvent;
        this.keyUpEvent = keyUpEvent;
        this.listening = false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show( final int width,
                      final int height,
                      final int padding ) {
        view = new LienzoPanelView( width + padding, height + padding );
        view.init( this );

    }

    public void destroy() {
        this.listening = false;
        view.destroy();
    }

    void onMouseOver() {
        this.listening = true;
    }

    void onMouseOut() {
        this.listening = false;
    }

    void onKeyPress( final int unicodeChar ) {
        if ( listening ) {
            final KeyboardEvent.Key key = getKey( unicodeChar );
            if ( null != key ) {
                keyPressEvent.fire( new KeyPressEvent( key ) );

            }

        }

    }

    void onKeyDown( final int unicodeChar ) {
        if ( listening ) {
            final KeyboardEvent.Key key = getKey( unicodeChar );
            if ( null != key ) {
                keyDownEvent.fire( new KeyDownEvent( key ) );

            }

        }

    }

    void onKeyUp( final int unicodeChar ) {
        if ( listening ) {
            final KeyboardEvent.Key key = getKey( unicodeChar );
            if ( null != key ) {
                keyUpEvent.fire( new KeyUpEvent( key ) );

            }

        }

    }

    private KeyboardEvent.Key getKey( final int unicodeChar ) {
        final KeyboardEvent.Key[] keys = KeyboardEvent.Key.values();
        for ( final KeyboardEvent.Key key : keys ) {
            final int c = key.getUnicharCode();
            if ( c == unicodeChar ) {
                return key;

            }

        }
        return null;

    }

}
