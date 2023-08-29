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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;

@Dependent
@Default
public class DockingAcceptorControlImpl
        extends AbstractAcceptorControl
        implements DockingAcceptorControl<AbstractCanvasHandler> {

    private static final int HOTSPOT = 10;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Inject
    public DockingAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void onInit(final WiresCanvas canvas) {
        canvas.getWiresManager().setDockingAcceptor(DOCKING_ACCEPTOR);
    }

    @Override
    protected void onDestroy(final WiresCanvas canvas) {
        canvas.getWiresManager().setDockingAcceptor(IDockingAcceptor.NONE);
    }

    @Override
    public boolean allow(final Node parent,
                         final Node child) {
        return evaluate(parent,
                        child,
                        command -> getCommandManager().allow(getCanvasHandler(),
                                                             command));
    }

    @Override
    public boolean accept(final Node parent,
                          final Node child) {
        return evaluate(parent,
                        child,
                        command -> getCommandManager().execute(getCanvasHandler(),
                                                               command));
    }

    private boolean evaluate(final Node parent,
                             final Node child,
                             final
                             Function<Command<AbstractCanvasHandler, CanvasViolation>, CommandResult<CanvasViolation>> executor) {
        return null != parent &&
                isCommandSuccess(executor.apply(canvasCommandFactory.updateDockNode(parent,
                                                                                    child)));
    }

    private final IDockingAcceptor DOCKING_ACCEPTOR = new IDockingAcceptor() {
        @Override
        public boolean dockingAllowed(final WiresContainer wiresContainer,
                                      final WiresShape wiresShape) {
            if (!isWiresViewAccept(wiresContainer,
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
            if (!isWiresViewAccept(wiresContainer,
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
