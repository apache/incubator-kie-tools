/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.canvas;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

@Dependent
@CaseManagementEditor
public class CaseManagementCanvasView extends WiresCanvasView {

    @Inject
    public CaseManagementCanvasView(@CaseManagementEditor WiresManagerFactory wiresManagerFactory) {
        super(wiresManagerFactory);
    }

    @Override
    public WiresCanvas.View addShape(final ShapeView<?> shapeView) {

        if (WiresUtils.isWiresShape(shapeView)) {
            WiresShape wiresShape = (WiresShape) shapeView;
            wiresManager.register(wiresShape, false);
            WiresUtils.assertShapeGroup(wiresShape.getGroup(), WiresCanvas.WIRES_CANVAS_GROUP_ID);
        } else if (WiresUtils.isWiresConnector(shapeView)) {
            //Don't render connectors

        } else {
            super.addShape(shapeView);
        }

        return this;
    }

    public AbstractCanvas.View addChildShape(final ShapeView<?> parent, final ShapeView<?> child, final int index) {

        CaseManagementShapeView parentCMView = (CaseManagementShapeView) parent;
        CaseManagementShapeView childCMView = (CaseManagementShapeView) child;

        parentCMView.addShape(childCMView, index);

        return this;
    }
}
