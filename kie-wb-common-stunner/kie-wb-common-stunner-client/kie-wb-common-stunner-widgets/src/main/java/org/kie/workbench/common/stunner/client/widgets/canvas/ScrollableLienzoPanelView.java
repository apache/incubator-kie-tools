/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;

public class ScrollableLienzoPanelView
        extends ScrollablePanel
        implements StunnerLienzoBoundsPanelView {

    public ScrollableLienzoPanelView() {
        super(StunnerBoundsProviderFactory.newProvider());
    }

    public ScrollableLienzoPanelView(final int width,
                                     final int height) {
        super(StunnerBoundsProviderFactory.newProvider(), width, height);
    }

    @Override
    public void setPresenter(final StunnerLienzoBoundsPanel presenter) {
        presenter.register(
                addKeyDownHandler(event -> presenter.onKeyDown(event.getNativeKeyCode()))
        );
        presenter.register(
                addKeyPressHandler(event -> presenter.onKeyPress(event.getUnicodeCharCode()))
        );
        presenter.register(
                addKeyUpHandler(event -> presenter.onKeyUp(event.getNativeKeyCode()))
        );
    }
}
