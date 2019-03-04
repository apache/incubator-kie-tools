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
package org.kie.workbench.common.stunner.cm.client.shape.view;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.widget.panel.Bounds;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;

public class CaseManagementDiagramShapeView extends CaseManagementShapeView {

    private Bounds dropZoneBounds;

    public CaseManagementDiagramShapeView(String name,
                                          SVGPrimitiveShape svgPrimitive,
                                          double width,
                                          double height,
                                          boolean resizable) {
        super(name, svgPrimitive, width, height, resizable);

        // initialize the bounds and drop zone
        this.dropZoneBounds = Bounds.build(0.0d, 0.0d, 0.0d, 0.0d);
        this.createDropZone(Bounds.build(0.0d, 0.0d, width, height));
    }

    @Override
    protected CaseManagementShapeView createGhostShapeView(String shapeLabel, double width, double height) {
        return new CaseManagementDiagramShapeView(shapeLabel,
                                                  new SVGPrimitiveShape(getShape().copy()),
                                                  width, height, false);
    }

    @Override
    protected ILayoutHandler createGhostLayoutHandler() {
        return new HorizontalStackLayoutManager();
    }

    // use the whole canvas as drop zone
    private void createDropZone(Bounds bounds) {
        if (Double.compare(bounds.getHeight(), dropZoneBounds.getHeight()) != 0
                || Double.compare(bounds.getWidth(), dropZoneBounds.getWidth()) != 0) {
            this.dropZoneBounds = Bounds.build(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

            MultiPath multiPath = new MultiPath().rect(0.0, 0.0, dropZoneBounds.getWidth(), dropZoneBounds.getHeight());
            multiPath.setDraggable(false);

            this.setDropZone(Optional.of(multiPath));
        }
    }

    @Override
    public Optional<MultiPath> getDropZone() {
        this.getCanvas()
                .ifPresent(c -> c.getPanelBounds().
                        ifPresent(this::createDropZone));

        return super.getDropZone();
    }
}
