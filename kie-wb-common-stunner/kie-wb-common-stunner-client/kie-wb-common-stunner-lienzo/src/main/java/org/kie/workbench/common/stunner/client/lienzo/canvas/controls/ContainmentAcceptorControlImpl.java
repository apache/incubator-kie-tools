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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
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
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
@Default
public class ContainmentAcceptorControlImpl extends AbstractAcceptorControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final Function<AbstractCanvasHandler, CanvasHighlight> highlightFactory;
    private CanvasHighlight canvasHighlight;

    @Inject
    public ContainmentAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this(canvasCommandFactory, CanvasHighlight::new);
    }

    ContainmentAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                   final Function<AbstractCanvasHandler, CanvasHighlight> highlightFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.highlightFactory = highlightFactory;
    }

    @Override
    protected void onInit(final WiresCanvas canvas) {
        canvas.getWiresManager().setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
        this.canvasHighlight = highlightFactory.apply(getCanvasHandler());
    }

    @Override
    protected void onDestroy(final WiresCanvas canvas) {
        canvas.getWiresManager().setContainmentAcceptor(IContainmentAcceptor.NONE);
        this.canvasHighlight.destroy();
        this.canvasHighlight = null;
    }

    @Override
    public boolean allow(final Element parent,
                         final Node[] children) {
        return evaluate(parent,
                        children,
                        command -> getCommandManager().allow(getCanvasHandler(),
                                                             command),
                        true);
    }

    @Override
    public boolean accept(final Element parent,
                          final Node[] children) {
        return evaluate(parent,
                        children,
                        command -> getCommandManager().execute(getCanvasHandler(),
                                                               command),
                        false);
    }

    private boolean evaluate(final Element parent,
                             final Node[] children,
                             final Function<Command<AbstractCanvasHandler, CanvasViolation>, CommandResult<CanvasViolation>> executor,
                             final boolean highlights) {
        // Cannot evaluate with no candidates.
        if (children == null || children.length == 0) {
            return false;
        }
        // Do not accept multiple containment if children do not share same parent instance.
        if (parent == null && children.length >= 2) {
            return false;
        }
        boolean success = true;
        if (!areInSameParent(parent, children)) {
            // Generate the commands and perform the execution.
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder =
                    new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                            .forward();
            for (final Node node : children) {
                builder.addCommand(canvasCommandFactory.updateChildNode((Node) parent,
                                                                        node));
            }
            if (builder.size() > 0) {
                final Command<AbstractCanvasHandler, CanvasViolation> command = builder.size() == 1 ?
                        builder.get(0) :
                        builder.build();
                final CommandResult<CanvasViolation> result =
                        executor.apply(command);
                success = isCommandSuccess(result);
                if (highlights && !success) {
                    canvasHighlight.invalid(result.getViolations());
                }
            }
        }

        if (success) {
            canvasHighlight.unhighLight();
        }
        return success;
    }

    static boolean areInSameParent(final Element parent,
                                   final Node[] children) {
        return Stream.of(children)
                .map(GraphUtils::getParent)
                .noneMatch(childParent -> !Objects.equals(parent, childParent));
    }

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer,
                                          final WiresShape[] shapes) {
            if (!isWiresParentAccept(wiresContainer)) {
                return false;
            }
            final Node parentNode = toNode(wiresContainer);
            return allow(parentNode,
                         toNodeArray(shapes));
        }

        @Override
        public boolean acceptContainment(final WiresContainer wiresContainer,
                                         final WiresShape[] shapes) {
            if (!isWiresParentAccept(wiresContainer)) {
                return false;
            }
            final Node parentNode = toNode(wiresContainer);
            return accept(parentNode,
                          toNodeArray(shapes));
        }

        private Node[] toNodeArray(final WiresShape[] shapes) {
            final List<Node> nodes = new ArrayList<>(shapes.length);
            for (final WiresShape shape : shapes) {
                final Node node = toNode(shape);
                if (null != node) {
                    nodes.add(node);
                }
            }
            return nodes.toArray(new Node[nodes.size()]);
        }

        private Node toNode(final WiresContainer shape) {
            return WiresUtils.getNode(getCanvasHandler(),
                                      shape);
        }
    };
}
