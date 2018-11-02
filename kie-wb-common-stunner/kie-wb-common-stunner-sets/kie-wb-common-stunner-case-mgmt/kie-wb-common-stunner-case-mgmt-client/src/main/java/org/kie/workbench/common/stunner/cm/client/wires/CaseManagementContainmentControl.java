/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresContainmentControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;

public class CaseManagementContainmentControl implements WiresContainmentControl {

    private final WiresContainmentControlImpl containmentControl;
    private final CaseManagementContainmentStateHolder state;

    public CaseManagementContainmentControl(final WiresParentPickerControlImpl parentPickerControl,
                                            final CaseManagementContainmentStateHolder state) {
        this(new WiresContainmentControlImpl(parentPickerControl),
             state);
    }

    CaseManagementContainmentControl(final WiresContainmentControlImpl containmentControl,
                                     final CaseManagementContainmentStateHolder state) {
        this.containmentControl = containmentControl;
        this.state = state;
    }

    @Override
    public WiresContainmentControl setEnabled(boolean enabled) {
        containmentControl.setEnabled(enabled);
        return this;
    }

    @Override
    public void onMoveStart(double x, double y) {

        containmentControl.onMoveStart(x, y);
        if (!(getShape() instanceof CaseManagementShapeView)) {
            state.setGhost(Optional.empty());
            state.setOriginalIndex(Optional.empty());
            state.setOriginalParent(Optional.empty());
            return;
        }

        state.setOriginalParent(Optional.ofNullable(getParent()));
        state.setOriginalIndex(Optional.ofNullable(getShapeIndex()));
        state.setGhost(Optional.ofNullable(((CaseManagementShapeView) getShape()).getGhost()));

        final WiresParentPickerControl.Index index = containmentControl.getParentPickerControl().getIndex();
        index.clear();
        if (state.getGhost().isPresent()) {
            index.exclude(state.getGhost().get());
        }

        if ((getParent() instanceof CaseManagementShapeView)) {
            ((CaseManagementShapeView) getParent()).logicallyReplace((CaseManagementShapeView) getShape(),
                                                                         state.getGhost().get());
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        containmentControl.onMove(dx,
                                  dy);

        //Handle moving ghost from one container to another
        final Optional<CaseManagementShapeView> ghost = state.getGhost();
        if (ghost.isPresent() && null != getParent() && null != getParent().getGroup()) {
            if (getWiresManager().getContainmentAcceptor().containmentAllowed(getParent(),
                                                                              new WiresShape[]{getShape()})) {

                // Comment out the code below to leave the ghost of a subprocess to the right column

//                final double mouseX = containmentControl.getParentPickerControl().getShapeLocationControl().getMouseStartX() + dx;
//                final double mouseY = containmentControl.getParentPickerControl().getShapeLocationControl().getMouseStartY() + dy;
//                final Point2D parentAbsLoc = getParent().getGroup().getComputedLocation();
//                final Point2D mouseRelativeLoc = new Point2D(mouseX - parentAbsLoc.getX(),
//                                                             mouseY - parentAbsLoc.getY());
                //Children contains m_ghost and others excluding m_shape. This therefore moves m_ghost within children.
//                getParent().getLayoutHandler().add(ghost.get(),
//                                                   getParent(),
//                                                   mouseRelativeLoc);

                containmentControl.getParentPickerControl().rebuildPicker();
            }
        }

        return false;
    }

    @Override
    public boolean onMoveComplete() {
        return containmentControl.onMoveComplete();
    }

    @Override
    public boolean isAllow() {
        return containmentControl.isAllow();
    }

    @Override
    public boolean accept() {

        if (null == getParent() || getParent() instanceof WiresLayer) {
            return false;
        }

        if (!(state.getOriginalParent().isPresent() || state.getOriginalIndex().isPresent())) {
            return false;
        }

        // Execute.
        return getWiresManager()
                .getContainmentAcceptor()
                .acceptContainment(getParent(),
                                   new WiresShape[]{getShape()});
    }

    @Override
    public Point2D getCandidateLocation() {
        return containmentControl.getCandidateLocation();
    }

    @Override
    public Point2D getAdjust() {
        return containmentControl.getAdjust();
    }

    @Override
    public void execute() {
        // Execute.
        final CaseManagementShapeView ghost = state.getGhost().get();
        //Children contains m_ghost and others excluding m_shape. This replaces m_ghost with m_shape.
        final WiresContainer originalParent = state.getOriginalParent().get();
        reparentDraggedShape(ghost,
                             originalParent);
        clearState();
        batch();
    }

    @Override
    public void clear() {
        clearState();
        containmentControl.clear();
    }

    private void clearState() {
        state.getGhost().ifPresent(WiresShape::removeFromParent);
        state.getGhost().ifPresent(WiresShapeViewExt::destroy);
        state.setGhost(Optional.empty());
        state.setOriginalIndex(Optional.empty());
        state.setOriginalParent(Optional.empty());
    }

    @Override
    public void reset() {
        state.getGhost().ifPresent(this::restore);
        clearState();
    }

    @Override
    public void destroy() {
        clearState();
        containmentControl.destroy();
    }

    private void restore(final CaseManagementShapeView ghost) {
        final WiresContainer originalParent = state.getOriginalParent().get();
        final int originalIndex = state.getOriginalIndex().get();
        restore(ghost,
                originalParent,
                originalIndex);
    }

    private void batch() {
        getShape().getGroup().getLayer().batch();
    }

    private WiresContainer getParent() {
        return containmentControl.getParent();
    }

    private WiresShape getShape() {
        return containmentControl.getShape();
    }

    private WiresManager getWiresManager() {
        return getShape().getWiresManager();
    }

    private Integer getShapeIndex() {
        if (!(getParent() instanceof CaseManagementShapeView) || getShape() == null) {
            return 0;
        }

        return ((CaseManagementShapeView) getParent()).getIndex(getShape());
    }

    private void restore(final CaseManagementShapeView ghost,
                         final WiresContainer originalParent,
                         final int originalIndex) {
        if (originalParent instanceof CaseManagementShapeView) {
            restore(ghost,
                    (CaseManagementShapeView) originalParent,
                    originalIndex);
        } else {
            restoreGhostParent(ghost);
        }
    }

    private void restore(final CaseManagementShapeView ghost,
                         final CaseManagementShapeView parent,
                         final int index) {
        ghost.removeFromParent();
        parent.addShape(getShape(),
                        index);
    }

    private void reparentDraggedShape(final CaseManagementShapeView ghost,
                                      final WiresContainer originalParent) {
        if (!(originalParent instanceof CaseManagementShapeView)) {
            restoreGhostParent(ghost);
        }

        // Comment out the code to avoid drawing shapes in the wrong column
//        else {
//            if (originalParent.getChildShapes().contains(ghost)) {
//                ((CaseManagementShapeView) originalParent).logicallyReplace(ghost,
//                                                                            (CaseManagementShapeView) getShape());
//            } else {
//                restoreGhostParent(ghost);
//            }
//        }

        containmentControl.execute();
    }

    private void restoreGhostParent(final CaseManagementShapeView ghost) {
        if (ghost.getParent() != null) {
            final CaseManagementShapeView ghostContainer = (CaseManagementShapeView) ghost.getParent();
            final int ghostIndex = ghostContainer.getIndex(ghost);
            if (ghostContainer instanceof CaseManagementShapeView) {
                restore(ghost,
                        ghostContainer,
                        ghostIndex);
            }
        }
    }
}
