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
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;

@ApplicationScoped
public class EdgeFactoryImpl extends AbstractElementFactory<Object, Definition<Object>, Edge<Definition<Object>, Node>>
        implements EdgeFactory<Object> {

    private final DefinitionManager definitionManager;

    protected EdgeFactoryImpl() {
        this(null);
    }

    @Inject
    public EdgeFactoryImpl(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return EdgeFactory.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Edge<Definition<Object>, Node> build(final String uuid,
                                                final Object definition) {
        final EdgeImpl edge = new EdgeImpl<>(uuid);
        if (null != definition) {
            ViewConnector<Object> content = new ViewConnectorImpl<>(definition,
                                                                    buildBounds());
            edge.setContent(content);
            appendLabels(edge.getLabels(),
                         definition);
        }
        return edge;
    }

    @Override
    public boolean accepts(final Object source) {
        return true;
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    // TODO: Review.
    private Bounds buildBounds() {
        return Bounds.create(0d, 0d, 30d, 30d);
    }
}
