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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Adds a connector shape into the canvas and sets its source node.
 */
public class AddConnectorCommand extends AbstractCanvasGraphCommand {

    private final Node source;
    private final Edge candidate;
    private final Connection connection;
    private final String shapeSetId;

    public AddConnectorCommand(final Node source,
                               final Edge candidate,
                               final Connection connection,
                               final String shapeSetId) {
        this.source = source;
        this.candidate = candidate;
        this.connection = connection;
        this.shapeSetId = shapeSetId;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand(source,
                                                                                                candidate,
                                                                                                connection);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new AddCanvasConnectorCommand(candidate,
                                             shapeSetId);
    }

    public Node getSource() {
        return source;
    }

    public Edge getCandidate() {
        return candidate;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [source=" + toUUID(getSource()) + "," +
                "candidate=" + toUUID(getCandidate()) + "," +
                "connection=" + connection + "," +
                "shapeSet=" + getShapeSetId() + "]";
    }
}
