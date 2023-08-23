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

import java.util.Collection;

import org.kie.workbench.common.dmn.client.commands.factory.graph.DMNDeleteElementsGraphCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DMNDeleteElementsCommand extends DeleteElementsCommand {

    private final DMNGraphsProvider graphsProvider;

    public DMNDeleteElementsCommand(final Collection<Element> elements,
                                    final DMNGraphsProvider graphsProvider) {
        super(elements);
        this.graphsProvider = graphsProvider;
    }

    public DMNGraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new DMNDeleteElementsGraphCommand(() -> elements,
                                                 new DMNCanvasMultipleDeleteProcessor(),
                                                 getGraphsProvider());
    }

    private class DMNCanvasMultipleDeleteProcessor extends CanvasMultipleDeleteProcessor {

        @Override
        protected DMNDeleteNodeCommand.DMNCanvasDeleteProcessor createProcessor(SafeDeleteNodeCommand.Options options) {
            return new DMNDeleteNodeCommand.DMNCanvasDeleteProcessor(options, getGraphsProvider());
        }
    }
}
