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

import java.util.Collection;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.client.commands.factory.canvas.DMNSafeDeleteNodeCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;

public class DMNDeleteElementsGraphCommand extends DeleteElementsCommand {

    private DMNGraphsProvider graphsProvider;

    public DMNDeleteElementsGraphCommand(final Supplier<Collection<Element>> elements,
                                         final DeleteCallback callback,
                                         final DMNGraphsProvider graphsProvider) {
        super(elements, callback);
        this.graphsProvider = graphsProvider;
    }

    @Override
    protected DMNSafeDeleteNodeCommand createSafeDeleteNodeCommand(final Node<?, Edge> node,
                                                                   final SafeDeleteNodeCommand.Options options,
                                                                   final DeleteCallback callback) {
        return new DMNSafeDeleteNodeCommand(node,
                                            callback.onDeleteNode(node, options),
                                            options,
                                            getGraphsProvider());
    }

    public DMNGraphsProvider getGraphsProvider() {
        return graphsProvider;
    }
}
