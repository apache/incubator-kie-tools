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

package org.kie.workbench.common.dmn.client.commands.factory;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.canvas.AddChildNodeCommand;
import org.kie.workbench.common.dmn.client.commands.factory.canvas.DMNDeleteElementsCommand;
import org.kie.workbench.common.dmn.client.commands.factory.canvas.DMNDeleteNodeCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

@DMNEditor
@ApplicationScoped
public class DefaultCanvasCommandFactory extends LienzoCanvasCommandFactory {

    private DMNGraphsProvider graphsProvider;

    protected DefaultCanvasCommandFactory() {
        super();
    }

    @Inject
    public DefaultCanvasCommandFactory(final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors,
                                       final ManagedInstance<ViewTraverseProcessor> viewTraverseProcessors,
                                       final @DMNEditor DMNGraphsProvider graphsProvider) {
        super(childrenTraverseProcessors,
              viewTraverseProcessors);
        this.graphsProvider = graphsProvider;
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addChildNode(final Node parent,
                                                             final Node candidate,
                                                             final String shapeSetId) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> delete(final Collection<Element> candidates) {
        return new DMNDeleteElementsCommand(candidates, graphsProvider);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteNode(final Node candidate) {
        return new DMNDeleteNodeCommand(candidate, graphsProvider);
    }
}
