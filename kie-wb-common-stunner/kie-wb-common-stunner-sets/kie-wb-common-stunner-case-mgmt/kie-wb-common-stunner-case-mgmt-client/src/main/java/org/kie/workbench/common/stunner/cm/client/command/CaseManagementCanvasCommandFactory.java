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

package org.kie.workbench.common.stunner.cm.client.command;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

@ApplicationScoped
@CaseManagementEditor
public class CaseManagementCanvasCommandFactory extends LienzoCanvasCommandFactory {

    @Inject
    public CaseManagementCanvasCommandFactory(final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors,
                                              final ManagedInstance<ViewTraverseProcessor> viewTraverseProcessors) {
        super(childrenTraverseProcessors,
              viewTraverseProcessors);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addNode(final Node candidate,
                                                        final String shapeSetId) {
        return new CaseManagementAddNodeCommand(candidate, shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> draw() {
        return new CaseManagementDrawCommand(newChildrenTraverseProcessor());
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addChildNode(final Node parent,
                                                             final Node child,
                                                             final String shapeSetId) {
        return new CaseManagementAddChildCommand(parent,
                                                 child,
                                                 shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setChildNode(final Node parent,
                                                             final Node child) {
        return new CaseManagementSetChildCommand(parent,
                                                 child);
    }

    public CanvasCommand<AbstractCanvasHandler> setChildNode(final Node<View<?>, Edge> parent,
                                                             final Node<View<?>, Edge> child,
                                                             final Optional<Node<View<?>, Edge>> last,
                                                             final OptionalInt index,
                                                             final Optional<Node<View<?>, Edge>> originalParent,
                                                             final OptionalInt originalIndex) {
        return new CaseManagementSetChildCommand(parent,
                                                 child,
                                                 last,
                                                 index,
                                                 originalParent,
                                                 originalIndex);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> removeChild(final Node parent,
                                                            final Node candidate) {
        return new CaseManagementRemoveChildCommand(parent,
                                                    candidate);
    }

    @Override
    //This command is used to update a Node location following 'Drag', 'Resize' or 'Add from Palette' operations
    //Case Management does not update the location of any Nodes, preserving the layout information that may have
    //been set using the full BPMN2 editor. This command equates to a NOP for Case Management.
    public CanvasCommand<AbstractCanvasHandler> updatePosition(final Node<View<?>, Edge> element,
                                                               final Point2D location) {
        return new CaseManagementUpdatePositionCommand(element,
                                                       location);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> cloneNode(Node candidate, String parentUuid, Point2D cloneLocation, Consumer<Node> callback) {
        return new CaseManagementCloneNodeCommand(candidate, parentUuid, cloneLocation, callback, getChildrenTraverseProcessors());
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> delete(Collection<Element> candidates) {
        return new CaseManagementDeleteElementsCommand(candidates);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteNode(Node candidate) {
        return new CaseManagementDeleteNodeCommand(candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> clearCanvas() {
        return new CaseManagementClearCommand();
    }
}
