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
package org.uberfire.ext.wires.core.grids.client.model;

import java.util.Arrays;
import java.util.Objects;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;

/**
 * Defines the action that triggers an Edit operation on a cell.
 */
public enum GridCellEditAction {

    NONE(AbstractNodeMouseEvent.class),
    SINGLE_CLICK(NodeMouseClickEvent.class),
    DOUBLE_CLICK(NodeMouseDoubleClickEvent.class);

    private Class<? extends AbstractNodeMouseEvent> eventClass;

    GridCellEditAction(final Class<? extends AbstractNodeMouseEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public static GridCellEditAction getSupportedEditAction(final AbstractNodeMouseEvent event) {
        return Arrays.stream(values())
                .filter(action -> Objects.equals(action.eventClass, event.getClass()))
                .findFirst()
                .orElse(NONE);
    }

}
