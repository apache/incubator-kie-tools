/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.wires;

import java.util.Objects;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import jakarta.enterprise.event.Event;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent;
import org.kie.workbench.common.stunner.core.client.shape.HasShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;

public class StunnerWiresShapeStateHighlight implements WiresShapeHighlight<PickerPart.ShapePart> {

    private final StunnerWiresShapeHighlight delegate;
    private HasShapeState current;
    private PickerPart.ShapePart currentPartState;

    public StunnerWiresShapeStateHighlight(final WiresManager wiresManager,
                                           final Event<CanvasUnhighlightEvent> unhighlightEvent) {
        this(new StunnerWiresShapeHighlight(wiresManager, unhighlightEvent));
    }

    StunnerWiresShapeStateHighlight(final StunnerWiresShapeHighlight delegate) {
        this.delegate = delegate;
    }

    @Override
    public void highlight(final WiresShape shape,
                          final PickerPart.ShapePart part) {
        highlight(shape,
                  part,
                  ShapeState.HIGHLIGHT);
    }

    @Override
    public void error(final WiresShape shape,
                      final PickerPart.ShapePart shapePart) {
        highlight(shape,
                  shapePart,
                  ShapeState.INVALID);
    }

    @Override
    public void restore() {
        if (null != current &&
                (Objects.isNull(currentPartState) || Objects.equals(PickerPart.ShapePart.BODY, currentPartState))) {
            current.applyState(ShapeState.NONE);
            setCurrent(null);
        } else {
            //always restore because the highlightBorder is not applying state, it is only delegating.
            delegate.restore();
        }
        currentPartState = null;
    }

    private void highlight(final WiresShape shape,
                           final PickerPart.ShapePart part,
                           final ShapeState state) {
        this.currentPartState = part;
        switch (part) {
            case BODY:
                highlightBody(shape,
                              state);
                break;
            default:
                highlightBorder(shape);
        }
    }

    private void highlightBody(final WiresShape shape,
                               final ShapeState state) {
        restore();
        if (shape instanceof HasShapeState) {
            setCurrent((HasShapeState) shape);
            current.applyState(state);
        } else {
            delegate.highlight(shape, PickerPart.ShapePart.BODY);
        }
    }

    private void highlightBorder(final WiresShape shape) {
        delegate.highlight(shape, PickerPart.ShapePart.BORDER);
    }

    void setCurrent(final HasShapeState current) {
        this.current = current;
    }
}
