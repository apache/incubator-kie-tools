/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.client.lienzo.shape.view.WiresConnectorViewExt;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;

public class BasicConnectorShape<W, D extends MutableShapeDef<W>, V extends WiresConnectorViewExt<?>>
        extends ConnectorShape<W, D, V> {

    private ShapeState state = ShapeState.NONE;
    private double _strokeWidth = 1;
    private double _strokeAlpha = 0;
    private String _strokeColor = null;

    public BasicConnectorShape(D shapeDef,
                               V view) {
        super(shapeDef,
              view);
    }

    @Override
    public void applyState(ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            switch (this.state) {
                case NONE:
                    applyNoneState();
                    break;
                default:
                    applyActiveState(shapeState.getColor());
            }
        }
    }

    /**
     * Use the connector's line as decorator for the different states.
     */
    protected void applyActiveState(final String color) {
        if (null == this._strokeColor) {
            this._strokeColor = getShapeView().getLine().getStrokeColor();
        }
        this._strokeWidth = getShapeView().getLine().getStrokeWidth();
        this._strokeAlpha = getShapeView().getLine().getStrokeAlpha();
        getShapeView().getLine().setStrokeWidth(5);
        getShapeView().getLine().setStrokeAlpha(1);
        getShapeView().getLine().setStrokeColor(color);
    }

    /**
     * Use the connector's line as decorator for the different states.
     */
    protected void applyNoneState() {
        if (null != this._strokeColor) {
            getShapeView().getLine().setStrokeColor(this._strokeColor);
            this._strokeColor = null;
        }
        getShapeView().getLine().setStrokeWidth(this._strokeWidth);
        getShapeView().getLine().setStrokeAlpha(this._strokeAlpha);
    }
}
