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


package org.kie.workbench.common.stunner.core.factory.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class NodeFactoryImpl extends AbstractElementFactory<Object, Definition<Object>, Node<Definition<Object>, Edge>>
        implements NodeFactory<Object> {

    private final DefinitionUtils definitionUtils;

    protected NodeFactoryImpl() {
        this(null);
    }

    @Inject
    public NodeFactoryImpl(final DefinitionUtils definitionUtils) {
        this.definitionUtils = definitionUtils;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return NodeFactory.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node<Definition<Object>, Edge> build(final String uuid,
                                                final Object definition) {
        final NodeImpl node = new NodeImpl<>(uuid);
        final Bounds bounds = definitionUtils.buildBounds(definition,
                                                          0d,
                                                          0d);
        View<Object> content = new ViewImpl<>(definition,
                                              bounds);
        node.setContent(content);
        appendLabels(node.getLabels(),
                     definition);
        return node;
    }

    @Override
    public boolean accepts(final Object source) {
        return true;
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }
}
