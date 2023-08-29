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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteConnectorCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class DMNDeleteConnectorCommand extends DeleteConnectorCommand {

    private final DMNGraphsProvider graphsProvider;

    public DMNDeleteConnectorCommand(final Edge<? extends View, Node> edge,
                                     final DMNGraphsProvider graphsProvider) {
        super(edge);
        this.graphsProvider = graphsProvider;
    }

    @Override
    protected SetConnectionTargetNodeCommand getSetConnectionTargetCommand(final Edge<? extends ViewConnector, Node> edge) {
        return new DMNSetConnectionTargetNodeCommand(null,
                                                     edge,
                                                     null,
                                                     graphsProvider);
    }

    @Override
    protected SetConnectionSourceNodeCommand getSetConnectionSourceCommand(final Edge<? extends ViewConnector, Node> edge) {
        return new DMNSetConnectionSourceNodeCommand(null,
                                                     edge,
                                                     null,
                                                     graphsProvider);
    }
}
