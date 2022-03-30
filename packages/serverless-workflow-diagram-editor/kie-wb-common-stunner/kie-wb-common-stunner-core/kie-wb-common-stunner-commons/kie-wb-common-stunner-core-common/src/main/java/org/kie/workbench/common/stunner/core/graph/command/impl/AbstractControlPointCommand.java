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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;

public abstract class AbstractControlPointCommand extends AbstractGraphCommand {

    private final String edgeUUID;

    public AbstractControlPointCommand(final String edgeUUID) {
        this.edgeUUID = PortablePreconditions.checkNotNull("edgeUUID", edgeUUID);
    }

    protected HasControlPoints getEdgeControlPoints(final GraphCommandExecutionContext context) {
        return (HasControlPoints) getEdge(context).getContent();
    }

    public String getEdgeUUID() {
        return edgeUUID;
    }

    public Edge getEdge(final GraphCommandExecutionContext context) {
        return context.getGraphIndex().getEdge(edgeUUID);
    }
}
