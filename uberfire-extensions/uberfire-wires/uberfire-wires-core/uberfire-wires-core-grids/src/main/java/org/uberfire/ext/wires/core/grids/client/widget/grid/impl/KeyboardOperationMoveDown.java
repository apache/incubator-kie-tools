/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import com.google.gwt.event.dom.client.KeyCodes;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollable;

public class KeyboardOperationMoveDown extends KeyboardOperationMove {

    public KeyboardOperationMoveDown(final GridLayer gridLayer) {
        this(gridLayer, null);
    }

    public KeyboardOperationMoveDown(final GridLayer gridLayer,
                                     final GridLienzoScrollable panel) {
        super(gridLayer, panel);
    }

    @Override
    SelectionExtension getSelectionExtension() {
        return SelectionExtension.DOWN;
    }

    @Override
    public int getKeyCode() {
        return KeyCodes.KEY_DOWN;
    }
}
