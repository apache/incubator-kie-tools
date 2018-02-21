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

package org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint;

import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.uberfire.workbench.events.UberFireEvent;

public abstract class CanvasControlPointEvent implements UberFireEvent {

    private final Point2D position;

    public CanvasControlPointEvent(final Point2D position) {
        this.position = position;
    }

    public Point2D getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "CanvasControlPointEvent{" +
                "position=" + position +
                '}';
    }
}