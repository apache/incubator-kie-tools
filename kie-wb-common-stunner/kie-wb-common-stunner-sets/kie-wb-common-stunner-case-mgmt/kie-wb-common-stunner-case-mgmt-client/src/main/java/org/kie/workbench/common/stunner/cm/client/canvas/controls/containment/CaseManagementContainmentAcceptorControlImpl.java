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
package org.kie.workbench.common.stunner.cm.client.canvas.controls.containment;

import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractContainmentBasedControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

@Dependent
@CaseManagementEditor
public class CaseManagementContainmentAcceptorControlImpl extends AbstractContainmentBasedControl
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private CanvasCommandFactory canvasCommandFactory;

    @Inject
    public CaseManagementContainmentAcceptorControlImpl(final @CaseManagementEditor CanvasCommandFactory canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void doEnable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
    }

    @Override
    protected void doDisable(final WiresCanvas.View view) {
        view.setContainmentAcceptor(IContainmentAcceptor.NONE);
    }

    @Override
    protected boolean isEdgeAccepted(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand(final Node parent,
                                                                                final Node child) {
        return canvasCommandFactory.setChildNode(parent,
                                                 child);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand(final Node parent,
                                                                                   final Node child) {
        return canvasCommandFactory.removeChild(parent,
                                                child);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean accept(final Node parent,
                          final Node child) {
        //The assumption is that if "allowed" passed then "accept" has already been validated
        return true;
    }

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer,
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
        public boolean acceptContainment(final WiresContainer wiresContainer,
                                         final WiresShape wiresShape) {

            // We have some interesting fun here; "accept" is called before the child WiresShape has been
            // added to the parent WiresShape or in the correct position (index) as determined by the parent's
            // ILayoutHandler. We therefore delay execution of the Command to mutate the Graph until the
            // WiresContainer has completed positioning the child.
            wiresContainer.setLayoutHandler(new InterceptingLayoutHandler(wiresContainer,
                                                                          makeGraphMutationCommand(wiresContainer,
                                                                                                   wiresShape)));

            return true;
        }
    };

    protected Optional<Command<AbstractCanvasHandler, CanvasViolation>> makeGraphMutationCommand(final WiresContainer wiresContainer,
                                                                                                 final WiresShape wiresShape) {
        final Node childNode = WiresUtils.getNode(getCanvasHandler(),
                                                  wiresShape);
        final Node parentNode = WiresUtils.getNode(getCanvasHandler(),
                                                   wiresContainer);

        if (parentNode == null && childNode == null) {
            return Optional.empty();
        }
        final Edge childEdge = getTheEdge(childNode);
        final boolean isSameParent = isSameParent(parentNode,
                                                  childEdge);
        final CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> builder = new CompositeCommandImpl
                .CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .reverse();

        if (isSameParent) {
            //TODO {manstis}
            //Given [A->B->C]
            // - A moves after C
            //    - need to delete A->B
            //    - need to add C->A

        } else {
            //TODO {manstis}
            //Given [A->B->C] and [D]
            // - B moves after D
            //    - need to delete A->B and B->C
            //    - need to add A->C
            //    - need to add D->B

            // Remove current relationship.
            if (null != childEdge && null != childEdge.getSourceNode()) {
                builder.addCommand(getDeleteEdgeCommand(childEdge.getSourceNode(),
                                                        childNode));
            }
            // Add a new relationship.
            builder.addCommand(getAddEdgeCommand(parentNode,
                                                 childNode));
        }

        return Optional.of(builder.build());
    }

    private class InterceptingLayoutHandler implements ILayoutHandler {

        private final WiresContainer container;
        private final ILayoutHandler layout;
        private final Optional<Command<AbstractCanvasHandler, CanvasViolation>> optCommand;

        private InterceptingLayoutHandler(final WiresContainer container,
                                          final Optional<Command<AbstractCanvasHandler, CanvasViolation>> optCommand) {
            this.container = container;
            this.layout = container.getLayoutHandler();
            this.optCommand = optCommand;
        }

        @Override
        public void add(final WiresShape shape,
                        final WiresContainer container,
                        final Point2D mouseRelativeLoc) {
            this.layout.add(shape,
                            container,
                            mouseRelativeLoc);
        }

        @Override
        public void remove(final WiresShape shape,
                           final WiresContainer container) {
            this.layout.remove(shape,
                               container);
        }

        @Override
        public void requestLayout(final WiresContainer container) {
            this.layout.requestLayout(container);
            this.container.setLayoutHandler(layout);
            executeCommand();
        }

        @Override
        public void layout(final WiresContainer container) {
            this.layout.layout(container);
        }

        private void executeCommand() {
            this.optCommand.ifPresent(cmd -> getCommandManager().execute(getCanvasHandler(),
                                                                         cmd));
        }
    }
}
