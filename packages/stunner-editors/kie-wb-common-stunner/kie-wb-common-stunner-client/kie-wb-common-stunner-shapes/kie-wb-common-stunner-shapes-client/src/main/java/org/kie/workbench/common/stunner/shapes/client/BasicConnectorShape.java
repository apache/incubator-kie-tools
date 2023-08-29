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


package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresConnectorViewExt;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;

public class BasicConnectorShape<W, D extends ShapeViewDef<W, V>, V extends WiresConnectorViewExt>
        extends ConnectorShape<W, D, V> {

    @SuppressWarnings("unchecked")
    public BasicConnectorShape(D shapeDef,
                               V view) {
        super(shapeDef,
              view,
              new ShapeStateDefaultHandler()
                      .setRenderType(ShapeStateDefaultHandler.RenderType.STROKE)
                      .setBorderShape(() -> view)
                      .setBackgroundShape(() -> view));
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        super.applyState(shapeState);
        if (!isSelected()) {
            getShapeView().hideControlPoints();
        }
    }

    private boolean isSelected() {
        return ShapeState.SELECTED.equals(getShape().getShapeStateHandler().getShapeState());
    }
}
