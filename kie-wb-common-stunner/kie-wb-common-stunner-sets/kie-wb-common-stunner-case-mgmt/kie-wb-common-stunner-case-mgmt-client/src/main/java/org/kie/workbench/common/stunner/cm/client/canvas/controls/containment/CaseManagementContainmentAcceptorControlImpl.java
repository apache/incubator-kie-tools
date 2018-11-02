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
package org.kie.workbench.common.stunner.cm.client.canvas.controls.containment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractAcceptorControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.cm.client.command.CaseManagementCanvasCommandFactory;
import org.kie.workbench.common.stunner.cm.client.wires.CaseManagementContainmentStateHolder;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

@Dependent
@CaseManagementEditor
public class CaseManagementContainmentAcceptorControlImpl extends AbstractAcceptorControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private final CaseManagementCanvasCommandFactory canvasCommandFactory;
    private final CaseManagementContainmentStateHolder state;
    final IContainmentAcceptor containmentAcceptor;

    @Inject
    public CaseManagementContainmentAcceptorControlImpl(final @CaseManagementEditor CaseManagementCanvasCommandFactory canvasCommandFactory,
                                                        final CaseManagementContainmentStateHolder state) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.state = state;
        this.containmentAcceptor = new CanvasManagementContainmentAcceptor();
    }

    @Override
    protected void onInit(final WiresCanvas.View view) {
        view.setContainmentAcceptor(containmentAcceptor);
    }

    @Override
    protected void onDestroy(final WiresCanvas.View view) {
        view.setContainmentAcceptor(IContainmentAcceptor.NONE);
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

    Command<AbstractCanvasHandler, CanvasViolation> getSetEdgeCommand(final Node parent,
                                                                      final Node child,
                                                                      final Optional<Integer> index,
                                                                      final Optional<Node> originalParent,
                                                                      final Optional<Integer> originalIndex) {
        return canvasCommandFactory.setChildNode(parent,
                                                 child,
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
                final int index = getAddIndex(wiresShapes[0], wiresContainer);
                if (index >= 0) {
                    final Optional<Integer> newIndex = Optional.of(index);
                    final Optional<WiresContainer> originalContainer = state.getOriginalParent();
                    final Optional<Integer> originalIndex = state.getOriginalIndex();
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

        protected Command<AbstractCanvasHandler, CanvasViolation> makeAddMutationCommand(final WiresShape shape,
                                                                                         final WiresContainer container,
                                                                                         final Optional<Integer> index,
                                                                                         final Optional<WiresContainer> originalContainer,
                                                                                         final Optional<Integer> originalIndex) {
            final Node parent = WiresUtils.getNode(getCanvasHandler(),
                                                   container);
            final Node child = WiresUtils.getNode(getCanvasHandler(),
                                                  shape);
            final Optional<Node> originalParent = originalContainer.flatMap((c) -> Optional.ofNullable(WiresUtils.getNode(getCanvasHandler(),
                                                                                                                          c)));

            // Set relationship.
            return getSetEdgeCommand(parent,
                                     child,
                                     index,
                                     originalParent,
                                     originalIndex);
        }

        int getAddIndex(final WiresShape wiresShape,
                                final WiresContainer container) {
            Node parent = WiresUtils.getNode(getCanvasHandler(), container);

            if (parent.getInEdges().size() == 0) {  // Add to the canvas horizontally
                final double shapeX = wiresShape.getComputedLocation().getX();

                // exclude the shape and its ghost
                final List<WiresShape> children  = container.getChildShapes().toList().stream()
                        .filter(s -> !((WiresShapeView) s).getUUID().equals(((WiresShapeView) wiresShape).getUUID()))
                        .collect(Collectors.toList());

                int targetIndex = children.size();

                for (int idx = 0; idx < children.size(); idx++) {
                    final WiresShape child = children.get(idx);
                    if (shapeX < child.getComputedLocation().getX()) {
                        targetIndex = idx;
                        break;
                    }
                }

                return targetIndex;
            } else {    // Add to a stage vertically
                if (state.getOriginalParent().isPresent() && state.getOriginalParent().get().equals(container)) {  // same stage
                    return parent.getOutEdges().size() - 1;
                } else {
                    return parent.getOutEdges().size();
                }
            }
        }
    }
}
