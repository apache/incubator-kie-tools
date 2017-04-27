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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresDockingAndContainmentControlImpl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class CaseManagementDockingAndContainmentControlImpl extends WiresDockingAndContainmentControlImpl {

    private final CaseManagementContainmentStateHolder state;

    public CaseManagementDockingAndContainmentControlImpl(final WiresShape shape,
                                                          final WiresManager wiresManager,
                                                          final CaseManagementContainmentStateHolder state) {
        super(shape,
              wiresManager);
        this.state = state;
    }

    @Override
    public void dragStart(final DragContext context) {
        super.dragStart(context);

        state.setGhost(Optional.empty());
        state.setOriginalIndex(Optional.empty());
        state.setOriginalParent(Optional.empty());

        if (!(m_shape instanceof AbstractCaseManagementShape)) {
            return;
        }

        state.setOriginalParent(Optional.ofNullable(m_parent));
        state.setOriginalIndex(Optional.ofNullable(getShapeIndex()));
        state.setGhost(Optional.ofNullable(((AbstractCaseManagementShape) m_shape).getGhost()));

        if ((m_parent instanceof AbstractCaseManagementShape)) {
            ((AbstractCaseManagementShape) m_parent).logicallyReplace(m_shape,
                                                                      state.getGhost().get());
        }
    }

    private Integer getShapeIndex() {
        if (m_parent == null || m_shape == null) {
            return null;
        }
        return m_parent.getChildShapes().toList().indexOf(m_shape);
    }

    @Override
    public boolean dragAdjust(final Point2D dxy) {
        super.dragAdjust(dxy);

        final double mouseX = m_mouseStartX + dxy.getX();
        final double mouseY = m_mouseStartY + dxy.getY();

        //Handle moving ghost from one container to another
        if (state.getGhost().isPresent() && m_parent != null) {
            if (m_parent.getContainmentAcceptor().containmentAllowed(m_parent,
                                                                     m_shape)) {
                final Point2D parentAbsLoc = WiresUtils.getLocation(m_parent.getGroup());
                final Point2D mouseRelativeLoc = new Point2D(mouseX - parentAbsLoc.getX(),
                                                             mouseY - parentAbsLoc.getY());

                //Children contains m_ghost and others excluding m_shape. This therefore moves m_ghost within children.
                m_parent.getLayoutHandler().add(state.getGhost().get(),
                                                m_parent,
                                                mouseRelativeLoc);
                m_layer.getLayer().batch();

                m_picker = makeColorMapBackedPicker(m_layer,
                                                    m_parent,
                                                    m_shape);
            }
        }

        return true;
    }

    @Override
    protected ColorMapBackedPicker makeColorMapBackedPicker(final WiresLayer layer,
                                                            final WiresContainer parent,
                                                            final WiresShape shape) {
        final NFastArrayList<WiresShape> shapesToSkip = new NFastArrayList<>();
        state.getGhost().ifPresent(shapesToSkip::add);
        shapesToSkip.add(shape);
        return new CaseManagementColorMapBackedPicker(layer.getChildShapes(),
                                                      layer.getLayer().getScratchPad(),
                                                      shapesToSkip,
                                                      shape.getDockingAcceptor().dockingAllowed(parent,
                                                                                                shape),
                                                      shape.getDockingAcceptor().getHotspotSize());
    }

    @Override
    protected boolean addShapeToParent() {
        if (!(state.getOriginalParent().isPresent() || state.getOriginalIndex().isPresent())) {
            return true;
        }
        if (!state.getGhost().isPresent()) {
            return true;
        }

        //Children contains m_ghost and others excluding m_shape. This replaces m_ghost with m_shape.
        final AbstractCaseManagementShape ghost = state.getGhost().get();
        final WiresContainer originalParent = state.getOriginalParent().get();
        final int originalIndex = state.getOriginalIndex().get();

        final DragEndMode mode = getDragEndMode();
        switch (mode) {
            case VETO:
                restore(ghost,
                        originalParent,
                        originalIndex);
                break;
            default:
                reparentDraggedShape(ghost,
                                     originalParent);
        }

        ghost.getChildShapes().toList().forEach(WiresShape::destroy);
        ghost.destroy();
        state.setGhost(Optional.empty());
        state.setOriginalIndex(Optional.empty());
        state.setOriginalParent(Optional.empty());
        m_layer.getLayer().batch();
        m_picker = null;
        return true;
    }

    private DragEndMode getDragEndMode() {
        if (m_parent == null) {
            return DragEndMode.VETO;
        } else if (!m_parent.getContainmentAcceptor().containmentAllowed(m_parent,
                                                                         m_shape)) {
            return DragEndMode.VETO;
        } else if (state.getGhost().isPresent()) {
            return DragEndMode.REMOVE_GHOST;
        }
        return DragEndMode.ADD;
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
        parent.addShape(m_shape,
                        index);
    }

    private void reparentDraggedShape(final AbstractCaseManagementShape ghost,
                                      final WiresContainer originalParent) {
        if (originalParent instanceof AbstractCaseManagementShape) {
            if (originalParent.getChildShapes().contains(ghost)) {
                ((AbstractCaseManagementShape) originalParent).logicallyReplace(ghost,
                                                                                m_shape);
            } else {
                restoreGhostParent(ghost);
            }
        } else {
            restoreGhostParent(ghost);
        }
        super.addShapeToParent();
    }

    private void restoreGhostParent(final AbstractCaseManagementShape ghost) {
        if (ghost.getParent() != null) {
            final WiresContainer ghostContainer = ghost.getParent();
            final int ghostIndex = ghostContainer.getChildShapes().toList().indexOf(ghost);
            if (ghostContainer instanceof AbstractCaseManagementShape) {
                restore(ghost,
                        (AbstractCaseManagementShape) ghostContainer,
                        ghostIndex);
            }
        }
    }

    private enum DragEndMode {
        VETO,
        REMOVE_GHOST,
        ADD
    }
}
