/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.canvas.view;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class LienzoPanel implements IsWidget {

    public interface View extends UberView<LienzoPanel> {

        void setPixelSize(int wide,
                          int high);

        void destroy();
    }

    private final Event<KeyPressEvent> keyPressEvent;
    private final Event<KeyDownEvent> keyDownEvent;
    private final Event<KeyUpEvent> keyUpEvent;
    private final Event<CanvasMouseDownEvent> mouseDownEvent;
    private final Event<CanvasMouseUpEvent> mouseUpEvent;
    private View view;

    private boolean listening;

    @Inject
    public LienzoPanel(final Event<KeyPressEvent> keyPressEvent,
                       final Event<KeyDownEvent> keyDownEvent,
                       final Event<KeyUpEvent> keyUpEvent,
                       final Event<CanvasMouseDownEvent> mouseDownEvent,
                       final Event<CanvasMouseUpEvent> mouseUpEvent) {
        this.keyPressEvent = keyPressEvent;
        this.keyDownEvent = keyDownEvent;
        this.keyUpEvent = keyUpEvent;
        this.mouseDownEvent = mouseDownEvent;
        this.mouseUpEvent = mouseUpEvent;
        this.listening = false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show(final int width,
                     final int height,
                     final int padding) {
        view = new LienzoPanelView(width + padding,
                                   height + padding);
        view.init(this);
    }

    public void setPixelSize(final int wide,
                             final int high) {
        view.setPixelSize(wide,
                          high);
    }

    public void destroy() {
        this.listening = false;
        view.destroy();
        view = null;
    }

    void onMouseDown() {
        if (listening) {
            mouseDownEvent.fire(new CanvasMouseDownEvent());
        }
    }

    void onMouseUp() {
        if (listening) {
            mouseUpEvent.fire(new CanvasMouseUpEvent());
        }
    }

    void onMouseOver() {
        this.listening = true;
    }

    void onMouseOut() {
        this.listening = false;
    }

    void onKeyPress(final int unicodeChar) {
        if (listening) {
            final KeyboardEvent.Key key = getKey(unicodeChar);
            if (null != key) {
                keyPressEvent.fire(new KeyPressEvent(key));
            }
        }
    }

    void onKeyDown(final int unicodeChar) {
        if (listening) {
            final KeyboardEvent.Key key = getKey(unicodeChar);
            if (null != key) {
                keyDownEvent.fire(new KeyDownEvent(key));
            }
        }
    }

    void onKeyUp(final int unicodeChar) {
        if (listening) {
            final KeyboardEvent.Key key = getKey(unicodeChar);
            if (null != key) {
                keyUpEvent.fire(new KeyUpEvent(key));
            }
        }
    }

    private KeyboardEvent.Key getKey(final int unicodeChar) {
        final KeyboardEvent.Key[] keys = KeyboardEvent.Key.values();
        for (final KeyboardEvent.Key key : keys) {
            final int c = key.getUnicharCode();
            if (c == unicodeChar) {
                return key;
            }
        }
        return null;
    }
}
