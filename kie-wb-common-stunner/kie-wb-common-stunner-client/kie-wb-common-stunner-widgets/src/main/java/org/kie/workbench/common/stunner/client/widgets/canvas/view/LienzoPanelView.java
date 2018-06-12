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

import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;

public class LienzoPanelView extends FocusableLienzoPanelView implements LienzoPanel.View {

    private final HandlerRegistrationImpl handlerRegistrationManager;
    private LienzoPanel presenter;

    public LienzoPanelView(final int width,
                           final int height) {

        this(width, height, new HandlerRegistrationImpl());
        initHandlers();
    }

    protected LienzoPanelView(final int width,
                              final int height,
                              HandlerRegistrationImpl handlerRegistrationImpl) {
        super(width, height);
        handlerRegistrationManager = handlerRegistrationImpl;
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

    protected void initHandlers() {

        handlerRegistrationManager.register(
                addMouseDownHandler(mouseDownEvent -> presenter.onMouseDown())
        );
        handlerRegistrationManager.register(
                addMouseUpHandler(mouseUpEvent -> presenter.onMouseUp())
        );
        handlerRegistrationManager.register(
                addKeyPressHandler(keyPressEvent -> {
                    final int unicodeChar = keyPressEvent.getUnicodeCharCode();
                    presenter.onKeyPress(unicodeChar);
                })
        );
        handlerRegistrationManager.register(
                addKeyDownHandler(keyDownEvent -> {
                    final int unicodeChar = keyDownEvent.getNativeKeyCode();
                    presenter.onKeyDown(unicodeChar);
                })
        );
        handlerRegistrationManager.register(
                addKeyUpHandler(keyUpEvent -> {
                    final int unicodeChar = keyUpEvent.getNativeKeyCode();
                    presenter.onKeyUp(unicodeChar);
                })
        );
    }
}
