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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getChildCanvasIndex;

public class CaseManagementCloneCanvasNodeCommand extends CloneCanvasNodeCommand {

    public CaseManagementCloneCanvasNodeCommand(final Node parent, final Node candidate, final String shapeSetId,
                                                final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        super(parent, candidate, shapeSetId, childrenTraverseProcessor);
    }

    @Override
    public AbstractCanvasCommand createAddCanvasChildNodeCommand(Node parent, Node candidate, String shapeSetId) {
        return new CaseManagementAddChildNodeCanvasCommand(parent, candidate, shapeSetId, getChildCanvasIndex(parent, candidate));
    }

    @Override
    public CloneCanvasNodeCommand createCloneCanvasNodeCommand(Node parent, Node candidate, String shapeSetId) {
        return new CaseManagementCloneCanvasNodeCommand(parent, candidate, getShapeSetId(), getChildrenTraverseProcessor());
    }
}
