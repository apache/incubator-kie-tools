/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.views;

import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.uberfire.mvp.Command;

public class LienzoPanelFocusHandler {

    HandlerRegistration overHandler;
    HandlerRegistration outHandler;

    public LienzoPanelFocusHandler listen(final LienzoPanel panel,
                                          final Command onFocus,
                                          final Command onLostFocus) {
        clear();
        overHandler = panel.getView().addMouseOverHandler(mouseOverEvent -> onFocus.execute());
        outHandler = panel.getView().addMouseOutHandler(mouseOutEvent -> onLostFocus.execute());
        return this;
    }

    public LienzoPanelFocusHandler clear() {
        if (null != overHandler) {
            overHandler.removeHandler();
            overHandler = null;
        }
        if (null != outHandler) {
            outHandler.removeHandler();
            outHandler = null;
        }
        return this;
    }
}
