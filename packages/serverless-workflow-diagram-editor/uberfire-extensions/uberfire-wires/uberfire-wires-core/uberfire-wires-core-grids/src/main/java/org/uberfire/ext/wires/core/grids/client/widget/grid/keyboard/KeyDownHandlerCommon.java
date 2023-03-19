/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard;

import java.util.Optional;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class KeyDownHandlerCommon implements KeyDownHandler {

    protected final GridLienzoPanel gridPanel;
    protected final GridLayer gridLayer;
    protected final GridWidget gridWidget;
    protected final HasSingletonDOMElementResource gridCell;

    private final boolean isTabKeyHandled;
    private final boolean isEnterKeyHandled;
    private final boolean isEscapeKeyHandled;

    public KeyDownHandlerCommon(final GridLienzoPanel gridPanel,
                                final GridLayer gridLayer,
                                final GridWidget gridWidget,
                                final HasSingletonDOMElementResource gridCell) {
        this(gridPanel,
             gridLayer,
             gridWidget,
             gridCell,
             true,
             true,
             true);
    }

    public KeyDownHandlerCommon(final GridLienzoPanel gridPanel,
                                final GridLayer gridLayer,
                                final GridWidget gridWidget,
                                final HasSingletonDOMElementResource gridCell,
                                final boolean isTabKeyHandled,
                                final boolean isEnterKeyHandled,
                                final boolean isEscapeKeyHandled) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.gridCell = gridCell;

        this.isTabKeyHandled = isTabKeyHandled;
        this.isEnterKeyHandled = isEnterKeyHandled;
        this.isEscapeKeyHandled = isEscapeKeyHandled;
    }

    @Override
    public void onKeyDown(final KeyDownEvent e) {
        final int keyCode = e.getNativeKeyCode();

        switch (keyCode) {
            case KeyCodes.KEY_TAB:
                if (isTabKeyHandled) {
                    flush(e);
                }
                break;

            case KeyCodes.KEY_ENTER:
                if (isEnterKeyHandled) {
                    flush(e);
                }
                break;

            case KeyCodes.KEY_ESCAPE:
                if (isEscapeKeyHandled) {
                    reset();
                }
        }

        e.stopPropagation();
    }

    private void flush(final KeyDownEvent e) {
        final int keyCode = e.getNativeKeyCode();
        final boolean isShiftKeyDown = e.isShiftKeyDown();

        gridCell.flush();
        moveSelection(keyCode, isShiftKeyDown);
        e.preventDefault();

        reset();
    }

    private void reset() {
        gridCell.destroyResources();
        gridPanel.setFocus(true);
        gridLayer.batch();
    }

    protected void moveSelection(final int keyCode,
                                 final boolean isShiftKeyDown) {
        final Optional<Integer> dx = getDelta(keyCode,
                                              KeyCodes.KEY_TAB,
                                              isShiftKeyDown);
        final Optional<Integer> dy = getDelta(keyCode,
                                              KeyCodes.KEY_ENTER,
                                              isShiftKeyDown);

        if (dx.isPresent()) {
            gridWidget.adjustSelection(dx.get() > 0 ? SelectionExtension.RIGHT : SelectionExtension.LEFT,
                                       false);
        }

        if (dy.isPresent()) {
            gridWidget.adjustSelection(dy.get() > 0 ? SelectionExtension.DOWN : SelectionExtension.UP,
                                       false);
        }
    }

    private Optional<Integer> getDelta(final int keyCode,
                                       final int requiredKeyCode,
                                       final boolean isShiftKeyDown) {
        if (keyCode == requiredKeyCode) {
            return Optional.of(isShiftKeyDown ? -1 : 1);
        }
        return Optional.empty();
    }
}