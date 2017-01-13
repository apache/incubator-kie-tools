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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.docking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractContainmentBasedControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;

@Dependent
public class DockingAcceptorControlImpl extends AbstractContainmentBasedControl<AbstractCanvasHandler>
        implements DockingAcceptorControl<AbstractCanvasHandler> {

    private static final int HOTSPOT = 10;
    private CanvasCommandFactory canvasCommandFactory;

    @Inject
    public DockingAcceptorControlImpl(final CanvasCommandFactory canvasCommandFactory,
                                      final @Request SessionCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        super(canvasCommandManager);
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void doEnable(final WiresCanvas.View view) {
        view.setDockingAcceptor(DOCKING_ACCEPTOR);
    }

    @Override
    protected void doDisable(final WiresCanvas.View view) {
        view.setDockingAcceptor(IDockingAcceptor.NONE);
    }

    @Override
    protected boolean isEdgeAccepted(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand(final Node parent,
                                                                                final Node child) {
        return canvasCommandFactory.dockNode(parent,
                                             child);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand(final Node parent,
                                                                                   final Node child) {
        return canvasCommandFactory.unDockNode(parent,
                                               child);
    }

    private final IDockingAcceptor DOCKING_ACCEPTOR = new IDockingAcceptor() {
        @Override
        public boolean dockingAllowed(final WiresContainer wiresContainer,
                                      final WiresShape wiresShape) {
            if (!isAccept(wiresContainer,
                          wiresShape)) {
                return false;
            }
            final Node childNode = WiresUtils.getNode(getCanvasHandler(),
                                                      wiresShape);
            final Node parentNode = WiresUtils.getNode(getCanvasHandler(),
                                                       wiresContainer);
            return allow(parentNode,
                         childNode);
        }

        @Override
        public boolean acceptDocking(final WiresContainer wiresContainer,
                                     final WiresShape wiresShape) {
            if (!isAccept(wiresContainer,
                          wiresShape)) {
                return false;
            }
            final Node childNode = WiresUtils.getNode(getCanvasHandler(),
                                                      wiresShape);
            final Node parentNode = WiresUtils.getNode(getCanvasHandler(),
                                                       wiresContainer);
            return accept(parentNode,
                          childNode);
        }

        @Override
        public int getHotspotSize() {
            return HOTSPOT;
        }
    };
}
