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

import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.diagram.AbstractDiagram;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;

/**
 * The default factory for generic Diagrams.
 */
@ApplicationScoped
public class DiagramFactoryImpl
        implements DiagramFactory<Metadata, Diagram<Graph, Metadata>> {

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    @Override
    public Diagram<Graph, Metadata> build(final String name,
                                          final Metadata metadata,
                                          final Graph<DefinitionSet, ?> graph) {
        final AbstractDiagram<Graph, Metadata> result = new DiagramImpl(name,
                                                                        metadata);
        result.setGraph(graph);
        return result;
    }

    @Override
    public boolean accepts(final String source) {
        return true;
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
