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
    public void onMoveStart(double x,
                            double y) {
        containmentControl.onMoveStart(x,
                                       y);

        if (!(getShape() instanceof AbstractCaseManagementShape)) {
            state.setGhost(Optional.empty());
            state.setOriginalIndex(Optional.empty());
            state.setOriginalParent(Optional.empty());
            return;
        }

        state.setOriginalParent(Optional.ofNullable(getParent()));
        state.setOriginalIndex(Optional.ofNullable(getShapeIndex()));
        state.setGhost(Optional.ofNullable(((AbstractCaseManagementShape) getShape()).getGhost()));

        final WiresParentPickerControl.Index index = containmentControl.getParentPickerControl().getIndex();
        index.clear();
        if (state.getGhost().isPresent()) {
            index.addShapeToSkip(state.getGhost().get());
        }

        if ((getParent() instanceof AbstractCaseManagementShape)) {
            ((AbstractCaseManagementShape) getParent()).logicallyReplace(getShape(),
                                                                         state.getGhost().get());
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        containmentControl.onMove(dx,
                                  dy);

        //Handle moving ghost from one container to another
        final Optional<AbstractCaseManagementShape> ghost = state.getGhost();
        if (ghost.isPresent() && null != getParent() && null != getParent().getGroup()) {
            if (getWiresManager().getContainmentAcceptor().containmentAllowed(getParent(),
                                                                              new WiresShape[]{getShape()})) {
                final double mouseX = containmentControl.getParentPickerControl().getShapeLocationControl().getMouseStartX() + dx;
                final double mouseY = containmentControl.getParentPickerControl().getShapeLocationControl().getMouseStartY() + dy;
                final Point2D parentAbsLoc = getParent().getGroup().getComputedLocation();
                final Point2D mouseRelativeLoc = new Point2D(mouseX - parentAbsLoc.getX(),
                                                             mouseY - parentAbsLoc.getY());
                //Children contains m_ghost and others excluding m_shape. This therefore moves m_ghost within children.
                getParent().getLayoutHandler().add(ghost.get(),
                                                   getParent(),
                                                   mouseRelativeLoc);

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
        final AbstractCaseManagementShape ghost = state.getGhost().get();
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

    private void restore(final AbstractCaseManagementShape ghost) {
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
        if (getParent() == null || getShape() == null) {
            return null;
        }
        return ((AbstractCaseManagementShape) getParent()).getIndex(getShape());
    }

    private void restore(final AbstractCaseManagementShape ghost,
                         final WiresContainer originalParent,
                         final int originalIndex) {
        if (originalParent instanceof AbstractCaseManagementShape) {
            restore(ghost,
                    (AbstractCaseManagementShape) originalParent,
                    originalIndex);
        } else {
            restoreGhostParent(ghost);
        }
    }

    private void restore(final AbstractCaseManagementShape ghost,
                         final AbstractCaseManagementShape parent,
                         final int index) {
        ghost.removeFromParent();
        parent.addShape(getShape(),
                        index);
    }

    private void reparentDraggedShape(final AbstractCaseManagementShape ghost,
                                      final WiresContainer originalParent) {
        if (originalParent instanceof AbstractCaseManagementShape) {
            if (originalParent.getChildShapes().contains(ghost)) {
                ((AbstractCaseManagementShape) originalParent).logicallyReplace(ghost,
                                                                                getShape());
            } else {
                restoreGhostParent(ghost);
            }
        } else {
            restoreGhostParent(ghost);
        }
        containmentControl.execute();
    }

    private void restoreGhostParent(final AbstractCaseManagementShape ghost) {
        if (ghost.getParent() != null) {
            final AbstractCaseManagementShape ghostContainer = (AbstractCaseManagementShape) ghost.getParent();
            final int ghostIndex = ghostContainer.getIndex(ghost);
            if (ghostContainer instanceof AbstractCaseManagementShape) {
                restore(ghost,
                        (AbstractCaseManagementShape) ghostContainer,
                        ghostIndex);
            }
        }
    }
}
