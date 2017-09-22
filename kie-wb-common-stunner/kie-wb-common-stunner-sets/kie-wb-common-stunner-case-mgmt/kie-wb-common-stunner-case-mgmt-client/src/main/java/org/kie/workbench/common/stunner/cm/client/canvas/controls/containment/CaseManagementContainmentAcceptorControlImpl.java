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

import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractAcceptorControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.cm.client.command.CaseManagementCanvasCommandFactory;
import org.kie.workbench.common.stunner.cm.client.wires.CaseManagementContainmentStateHolder;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

@Dependent
@CaseManagementEditor
public class CaseManagementContainmentAcceptorControlImpl extends AbstractAcceptorControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new CanvasManagementContainmentAcceptor();

    private final CaseManagementCanvasCommandFactory canvasCommandFactory;
    private final CaseManagementContainmentStateHolder state;

    @Inject
    public CaseManagementContainmentAcceptorControlImpl(final @CaseManagementEditor CaseManagementCanvasCommandFactory canvasCommandFactory,
                                                        final CaseManagementContainmentStateHolder state) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.state = state;
    }

    @Override
    protected void onEnable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
    }

    @Override
    protected void onDisable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(IContainmentAcceptor.NONE);
    }

    @Override
    public boolean allow(final Element parent,
                         final Node child) {
        return evaluate(parent,
                        child,
                        command -> getCommandManager().allow(getCanvasHandler(),
                                                             command));
    }

    @Override
    public boolean accept(final Element parent,
                          final Node child) {
        throw new UnsupportedOperationException();
    }

    private boolean evaluate(final Element parent,
                             final Node child,
                             final Function<Command<AbstractCanvasHandler, CanvasViolation>, CommandResult<CanvasViolation>> executor) {
        if (parent == null && child == null) {
            return false;
        }
        final Optional<Edge<?, Node>> edge = getFirstIncomingEdge(child,
                                                                  e -> e.getContent() instanceof Child);
        if (edge.isPresent()) {
            final CompositeCommand<AbstractCanvasHandler, CanvasViolation> command = buildCommands(parent,
                                                                                                   child,
                                                                                                   edge.get());
            final CommandResult<CanvasViolation> result = executor.apply(command);
            return isCommandSuccess(child,
                                    result);
        }
        return true;
    }

    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildCommands(final Element parent,
                                                                                   final Node child,
                                                                                   final Edge edge) {
        final CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> builder =
                new CompositeCommandImpl
                        .CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
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
            // Check containment is allowed. This (almost) replicates AbstractContainmentBasedControl.accept()
            // No need to checked whether a Child can be removed from a Container.
            final boolean isAccept = containmentAllowed(wiresContainer,
                                                        wiresShape);

            // We have some interesting fun here; "accept" is called before the child WiresShape has been
            // added to the parent WiresShape or in the correct position (index) as determined by the parent's
            // ILayoutHandler. We therefore delay execution of the Command to mutate the Graph until the
            // WiresContainer has completed positioning the child.
            if (isAccept) {
                wiresContainer.setLayoutHandler(new InterceptingLayoutHandler(wiresContainer));
            }
            return isAccept;
        }
    }

    class InterceptingLayoutHandler implements ILayoutHandler {

        private final WiresContainer container;
        private final ILayoutHandler layout;

        private InterceptingLayoutHandler(final WiresContainer container) {
            this.container = container;
            this.layout = container.getLayoutHandler();
        }

        @Override
        public void add(final WiresShape shape,
                        final WiresContainer container,
                        final Point2D mouseRelativeLoc) {
            try {

                this.layout.add(shape,
                                container,
                                mouseRelativeLoc);

                final WiresContainer newContainer = shape.getParent();
                final Optional<Integer> newIndex = Optional.of(newContainer.getChildShapes().toList().indexOf(shape));
                final Optional<WiresContainer> originalContainer = state.getOriginalParent();
                final Optional<Integer> originalIndex = state.getOriginalIndex();

                getCommandManager().execute(getCanvasHandler(),
                                            makeAddMutationCommand(shape,
                                                                   newContainer,
                                                                   newIndex,
                                                                   originalContainer,
                                                                   originalIndex));
            } finally {
                this.container.setLayoutHandler(layout);
            }
        }

        @Override
        public void remove(final WiresShape shape,
                           final WiresContainer container) {
            try {
                this.layout.remove(shape,
                                   container);
            } finally {
                this.container.setLayoutHandler(layout);
            }
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

        @Override
        public void requestLayout(final WiresContainer container) {
            this.layout.requestLayout(container);
        }

        @Override
        public void layout(final WiresContainer container) {
            this.layout.layout(container);
        }
    }
}
