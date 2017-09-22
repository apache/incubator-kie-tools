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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

@Dependent
public class ContainmentAcceptorControlImpl extends AbstractAcceptorControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private CanvasHighlight canvasHighlight;

    @Inject
    public ContainmentAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void onEnable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
        this.canvasHighlight = new CanvasHighlight(getCanvasHandler());
    }

    @Override
    protected void onDisable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(IContainmentAcceptor.NONE);
        this.canvasHighlight.destroy();
        this.canvasHighlight = null;
    }

    @Override
    public boolean allow(final Element parent,
                         final Node candidate) {
        return evaluate(parent,
                        candidate,
                        command -> getCommandManager().allow(getCanvasHandler(),
                                                             command),
                        true);
    }

    @Override
    public boolean accept(final Element parent,
                          final Node candidate) {
        return evaluate(parent,
                        candidate,
                        command -> getCommandManager().execute(getCanvasHandler(),
                                                               command),
                        false);
    }

    private boolean evaluate(final Element parent,
                             final Node candidate,
                             final Function<Command<AbstractCanvasHandler, CanvasViolation>, CommandResult<CanvasViolation>> executor,
                             final boolean highlightInvalid) {
        if (parent == null && candidate == null) {
            return false;
        }
        final CommandResult<CanvasViolation> result =
                executor.apply(canvasCommandFactory.updateChildNode((Node) parent,
                                                                    candidate));
        if (highlightInvalid && CommandUtils.isError(result)) {
            canvasHighlight.invalid(result.getViolations());
        } else {
            canvasHighlight.unhighLight();
        }
        return isCommandSuccess(candidate,
                                result);
    }

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer,
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
        public boolean acceptContainment(final WiresContainer wiresContainer,
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
    };
}
