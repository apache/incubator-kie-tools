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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.cm.client.wires.VerticalStackLayoutManager;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class CaseManagementShapeView extends SVGShapeViewImpl implements HasSize<SVGShapeViewImpl> {

    private final double minWidth;
    private final double minHeight;
    private final Optional<MultiPath> optDropZone;
    private double currentWidth;
    private double currentHeight;
    private String shapeLabel;
    private SVGPrimitiveShape primitiveShape;

    public CaseManagementShapeView(final String name,
                                   final SVGPrimitiveShape svgPrimitive,
                                   final double width,
                                   final double height,
                                   final boolean resizable) {
        super(name, svgPrimitive, width, height, resizable);
        this.minWidth = width;
        this.minHeight = height;
        this.currentWidth = minWidth;
        this.currentHeight = minHeight;
        this.optDropZone = makeDropZone();
        this.optDropZone.ifPresent((dz) -> dz.setDraggable(false));
        this.primitiveShape = svgPrimitive;
    }

    public void setLabel(String shapeLabel) {
        this.shapeLabel = shapeLabel;
        setTitle(shapeLabel);
        getTextViewDecorator().moveTitleToTop();
        refresh();
    }

    public double getWidth() {
        return currentWidth;
    }

    public double getHeight() {
        return currentHeight;
    }

    public void logicallyReplace(final CaseManagementShapeView original, final CaseManagementShapeView replacement) {

        if (original == null || replacement == null || replacement.getParent() == this) {
            return;
        }
        getChildShapes().set(getIndex(original), replacement);
        getContainer().getChildNodes().set(getNodeIndex(original.getGroup()), replacement.getGroup());

        original.setParent(null);
        replacement.setParent(this);

        getLayoutHandler().requestLayout(this);
    }

    public void addShape(final WiresShape shape, final int targetIndex) {

        if (shape == null || (targetIndex < 0 || targetIndex > getChildShapes().size())) {
            return;
        }

        final List<WiresShape> childShapes = getChildShapes().toList();
        childShapes.forEach(WiresShape::removeFromParent);

        // exclude the shape and its ghost
        final List<WiresShape> existingChildShapes = childShapes.stream()
                .filter(s -> !((WiresShapeView) s).getUUID().equals(((WiresShapeView) shape).getUUID()))
                .collect(Collectors.toList());

        existingChildShapes.add(targetIndex, shape);

        //call to add(..) causes ILayoutHandler to be invoked
        existingChildShapes.forEach(this::add);
    }

    public int getIndex(final WiresShape shape) {

        final NFastArrayList<WiresShape> children = getChildShapes();
        int i = 0;
        for (WiresShape child : children) {
            if (child == shape || isUUIDSame(shape, child)) {
                return i;
            }
            i++;
        }
        return i;
    }

    private boolean isUUIDSame(WiresShape shape, WiresShape child) {

        if (!(shape instanceof CaseManagementShapeView) || !(child instanceof CaseManagementShapeView)) {
            return false;
        }
        CaseManagementShapeView shapeCMView = (CaseManagementShapeView) shape;
        CaseManagementShapeView childCMView = (CaseManagementShapeView) child;

        return shapeCMView.getUUID().equals(childCMView.getUUID());
    }

    private int getNodeIndex(final Group group) {
        return getContainer().getChildNodes().toList().indexOf(group);
    }

    protected Optional<MultiPath> makeDropZone() {
        return Optional.empty();
    }

    public Optional<MultiPath> getDropZone() {
        return optDropZone;
    }

    public CaseManagementShapeView getGhost() {
        final CaseManagementShapeView ghost = createGhost();
        if (null != ghost) {
            ghost.setFillColor(ColorName.GRAY.getColorString());
            ghost.setFillAlpha(0.5d);
            ghost.setStrokeAlpha(0.5d);
            ghost.setUUID(getUUID());
        }
        return ghost;
    }

    protected CaseManagementShapeView createGhost() {

        CaseManagementShapeView thisGhost = new CaseManagementShapeView(shapeLabel,
                                                                        new SVGPrimitiveShape(getShape().copy()),
                                                                        currentWidth, currentHeight, false);
        thisGhost.setLayoutHandler(new VerticalStackLayoutManager());

        for (WiresShape wiresShape : getChildShapes()) {
            final CaseManagementShapeView childGhost = ((CaseManagementShapeView) wiresShape).getGhost();
            thisGhost.add(childGhost);
        }
        return thisGhost;
    }
}
