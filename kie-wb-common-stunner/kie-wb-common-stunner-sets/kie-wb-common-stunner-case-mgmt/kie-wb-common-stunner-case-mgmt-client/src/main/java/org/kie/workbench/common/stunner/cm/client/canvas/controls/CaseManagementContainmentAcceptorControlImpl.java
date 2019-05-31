/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.canvas.controls;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractAcceptorControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.cm.client.command.CaseManagementCanvasCommandFactory;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.client.wires.CaseManagementContainmentStateHolder;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
@CaseManagementEditor
public class CaseManagementContainmentAcceptorControlImpl
        extends AbstractAcceptorControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    final IContainmentAcceptor containmentAcceptor;
    private final CaseManagementCanvasCommandFactory canvasCommandFactory;
    private final CaseManagementContainmentStateHolder state;

    @Inject
    public CaseManagementContainmentAcceptorControlImpl(final @CaseManagementEditor CaseManagementCanvasCommandFactory canvasCommandFactory,
                                                        final CaseManagementContainmentStateHolder state) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.state = state;
        this.containmentAcceptor = new CanvasManagementContainmentAcceptor();
    }

    @Override
    protected void onInit(final WiresCanvas canvas) {
        canvas.getWiresManager().setContainmentAcceptor(containmentAcceptor);
    }

    @Override
    protected void onDestroy(final WiresCanvas canvas) {
        canvas.getWiresManager().setContainmentAcceptor(IContainmentAcceptor.NONE);
    }

    @Override
    public boolean allow(final Element parent,
                         final Node[] children) {
        return evaluate(parent,
                        children,
                        command -> getCommandManager().allow(getCanvasHandler(),
                                                             command));
    }

    @Override
    public boolean accept(final Element parent,
                          final Node[] children) {
        throw new UnsupportedOperationException();
    }

    private boolean evaluate(final Element parent,
                             final Node[] children,
                             final Function<Command<AbstractCanvasHandler, CanvasViolation>, CommandResult<CanvasViolation>> executor) {
        if (parent == null && (children == null || children.length == 0)) {
            return false;
        }
        final Node child = children[0];
        final Optional<Edge<?, Node>> edge = getFirstIncomingEdge(child,
                                                                  e -> e.getContent() instanceof Child);
        if (edge.isPresent()) {
            final Command<AbstractCanvasHandler, CanvasViolation> command = buildCommands(parent,
                                                                                          child,
                                                                                          edge.get());
            final CommandResult<CanvasViolation> result = executor.apply(command);
            return isCommandSuccess(result);
        }
        return true;
    }

    private Command<AbstractCanvasHandler, CanvasViolation> buildCommands(final Element parent,
                                                                          final Node child,
                                                                          final Edge edge) {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder =
                new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                        .reverse();
        if (null != edge.getSourceNode()) {
            builder.addCommand(
                    canvasCommandFactory.removeChild(edge.getSourceNode(),
                                                     child)
            );
        }
        if (null != parent) {
            builder.addCommand(
                    canvasCommandFactory.setChildNode((Node) parent,
                                                      child)
            );
        }
        return builder.build();
    }

    Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand(final Node parent,
                                                                      final Node child) {
        return canvasCommandFactory.setChildNode(parent,
                                                 child);
    }

    Command<AbstractCanvasHandler, CanvasViolation> getSetEdgeCommand(final Node<View<?>, Edge> parent,
                                                                      final Node<View<?>, Edge> child,
                                                                      final Optional<Node<View<?>, Edge>> last,
                                                                      final OptionalInt index,
                                                                      final Optional<Node<View<?>, Edge>> originalParent,
                                                                      final OptionalInt originalIndex) {
        return canvasCommandFactory.setChildNode(parent,
                                                 child,
                                                 last,
                                                 index,
                                                 originalParent,
                                                 originalIndex);
    }

    Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand(final Node parent,
                                                                         final Node child) {
        return canvasCommandFactory.removeChild(parent,
                                                child);
    }

    class CanvasManagementContainmentAcceptor implements IContainmentAcceptor {

        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer,
                                          final WiresShape[] wiresShapes) {
            final WiresShape wiresShape = wiresShapes[0];
            if (!isWiresViewAccept(wiresContainer,
                                   wiresShape)) {
                return false;
            }
            final Node childNode = WiresUtils.getNode(getCanvasHandler(),
                                                      wiresShape);
            final Node parentNode = WiresUtils.getNode(getCanvasHandler(),
                                                       wiresContainer);
            return allow(parentNode,
                         new Node[]{childNode});
        }

        @Override
        public boolean acceptContainment(final WiresContainer wiresContainer,
                                         final WiresShape[] wiresShapes) {
            if (state.getGhost().isPresent() &&
                    containmentAllowed(wiresContainer,
                                       wiresShapes)) {
                final CaseManagementShapeView container = (CaseManagementShapeView) wiresContainer;
                final CaseManagementShapeView ghost = state.getGhost().get();
                final int index = container.getIndex(ghost);
                if (index >= 0) {
                    final OptionalInt newIndex = OptionalInt.of(index);
                    final Optional<WiresContainer> originalContainer = state.getOriginalParent();
                    final OptionalInt originalIndex = state.getOriginalIndex();
                    final CommandResult<CanvasViolation> result =
                            getCommandManager().execute(getCanvasHandler(),
                                                        makeAddMutationCommand(wiresShapes[0],
                                                                               wiresContainer,
                                                                               newIndex,
                                                                               originalContainer,
                                                                               originalIndex));
                    return !CommandUtils.isError(result);
                }
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        protected Command<AbstractCanvasHandler, CanvasViolation> makeAddMutationCommand(final WiresShape shape,
                                                                                         final WiresContainer container,
                                                                                         final OptionalInt index,
                                                                                         final Optional<WiresContainer> originalContainer,
                                                                                         final OptionalInt originalIndex) {
            final Node parent = WiresUtils.getNode(getCanvasHandler(), container);
            final Node child = WiresUtils.getNode(getCanvasHandler(), shape);
            final int i = index.orElse(0);
            final Optional<Node<View<?>, Edge>> last = i > 0 ? Optional.of(WiresUtils.getNode(getCanvasHandler(), container.getChildShapes().get(i - 1))) : Optional.empty();

            final Optional<Node<View<?>, Edge>> originalParent = originalContainer.flatMap((c) -> Optional.ofNullable(WiresUtils.getNode(getCanvasHandler(), c)));

            // Set relationship.
            return getSetEdgeCommand(parent,
                                     child,
                                     last,
                                     index,
                                     originalParent,
                                     originalIndex);
        }
    }
}
