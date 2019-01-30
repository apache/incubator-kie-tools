/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.OptionalInt;
import java.util.function.Consumer;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.CloneNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.RegisterNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CaseManagementCloneNodeCommand extends CloneNodeCommand {

    protected CaseManagementCloneNodeCommand() {
        super();
    }

    public CaseManagementCloneNodeCommand(@MapsTo("candidate") Node candidate,
                                          @MapsTo("parentUuid") String parentUuid) {
        super(candidate, parentUuid);
    }

    public CaseManagementCloneNodeCommand(Node candidate,
                                          String parentUuid,
                                          Point2D position,
                                          Consumer<Node> callback,
                                          ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        super(candidate, parentUuid, position, callback, childrenTraverseProcessor);
    }

    public CaseManagementCloneNodeCommand(Node candidate,
                                          String parentUuid,
                                          Point2D position,
                                          Consumer<Node> callback) {
        super(candidate, parentUuid, position, callback);
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        return new CaseManagementSafeDeleteNodeCommand(getClone()).execute(context);
    }

    @Override
    protected void createNodeCommands(final Node<View, Edge> clone,
                                      final String parentUUID,
                                      final Point2D position) {
        addCommand(new RegisterNodeCommand(clone));
        addCommand(new CaseManagementAddChildNodeGraphCommand(parentUUID, clone, OptionalInt.empty()));
    }

    @Override
    protected CloneNodeCommand createCloneChildCommand(final Node candidate,
                                                       final String parentUuid,
                                                       final Point2D position,
                                                       final Consumer<Node> callback,
                                                       final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        return new CaseManagementCloneNodeCommand(candidate,
                                                  parentUuid,
                                                  position,
                                                  callback,
                                                  childrenTraverseProcessor);
    }
}
