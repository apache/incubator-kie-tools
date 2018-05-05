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

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;

public class LienzoPanelView extends FocusableLienzoPanelView implements LienzoPanel.View {

    private final HandlerRegistrationImpl handlerRegistrationManager = new HandlerRegistrationImpl();
    private LienzoPanel presenter;

    public LienzoPanelView(final int width,
                           final int height) {
        super(width,
              height);
        handlerRegistrationManager.register(
                addMouseOverHandler(mouseOverEvent -> presenter.onMouseOver())
        );
        handlerRegistrationManager.register(
                addMouseOutHandler(mouseOutEvent -> presenter.onMouseOut())
        );
        handlerRegistrationManager.register(
                addMouseDownHandler(event -> presenter.onMouseDown())
        );
        handlerRegistrationManager.register(
                addMouseUpHandler(event -> presenter.onMouseUp())
        );
        handlerRegistrationManager.register(
                RootPanel.get().addDomHandler(keyPressEvent -> {
                                                  final int unicodeChar = keyPressEvent.getUnicodeCharCode();
                                                  presenter.onKeyPress(unicodeChar);
                                              },
                                              KeyPressEvent.getType())
        );
        handlerRegistrationManager.register(
                RootPanel.get().addDomHandler(keyDownEvent -> {
                                                  final int unicodeChar = keyDownEvent.getNativeKeyCode();
                                                  presenter.onKeyDown(unicodeChar);
                                              },
                                              KeyDownEvent.getType())
        );
        handlerRegistrationManager.register(
                RootPanel.get().addDomHandler(keyUpEvent -> {
                                                  final int unicodeChar = keyUpEvent.getNativeKeyCode();
                                                  presenter.onKeyUp(unicodeChar);
                                              },
                                              KeyUpEvent.getType())
        );
    }

    @Override
    public void init(final LienzoPanel presenter) {
        this.presenter = presenter;
    }

    @Override
    public void destroy() {
        handlerRegistrationManager.removeHandler();
        presenter = null;
        super.destroy();
    }
}
