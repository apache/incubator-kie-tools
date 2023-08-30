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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DMNDeleteNodeCommand extends DeleteNodeCommand {

    private final DMNGraphsProvider graphsProvider;

    public DMNDeleteNodeCommand(final Node candidate,
                                final DMNGraphsProvider graphsProvider) {
        super(candidate,
              SafeDeleteNodeCommand.Options.defaults(),
              new DMNCanvasDeleteProcessor(SafeDeleteNodeCommand.Options.defaults(), graphsProvider));
        this.graphsProvider = graphsProvider;
    }

    public DMNGraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new DMNSafeDeleteNodeCommand(candidate,
                                            deleteProcessor,
                                            options,
                                            getGraphsProvider());
    }

    public static class DMNCanvasDeleteProcessor extends CanvasDeleteProcessor {

        private final DMNGraphsProvider graphsProvider;

        public DMNCanvasDeleteProcessor(final SafeDeleteNodeCommand.Options options,
                                        final DMNGraphsProvider graphsProvider) {
            super(options);
            this.graphsProvider = graphsProvider;
        }

        @Override
        protected DeleteCanvasConnectorCommand createDeleteCanvasConnectorNodeCommand(final Edge<? extends View<?>, Node> connector) {
            return new DMNDeleteCanvasConnectorNodeCommand(connector, graphsProvider);
        }

        @Override
        protected DMNDeleteCanvasNodeCommand createDeleteCanvasNodeCommand(final Node<?, Edge> node) {
            return new DMNDeleteCanvasNodeCommand(node, graphsProvider);
        }
    }
}
