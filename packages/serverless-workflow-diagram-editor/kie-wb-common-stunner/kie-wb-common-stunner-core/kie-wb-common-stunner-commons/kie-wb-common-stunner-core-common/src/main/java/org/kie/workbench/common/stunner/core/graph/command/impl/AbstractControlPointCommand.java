/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;

public abstract class AbstractControlPointCommand extends AbstractGraphCommand {

    private final String edgeUUID;

    protected AbstractControlPointCommand(final String edgeUUID) {
        this.edgeUUID = Objects.requireNonNull(edgeUUID, "Parameter named 'edgeUUID' should be not null!");
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
