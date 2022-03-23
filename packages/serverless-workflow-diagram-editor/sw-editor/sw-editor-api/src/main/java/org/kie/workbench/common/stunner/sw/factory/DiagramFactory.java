/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.factory;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.sw.Definitions;

// TODO: Really need for this?
@ApplicationScoped
public class DiagramFactory extends BindableDiagramFactory<Metadata, Diagram<Graph, Metadata>> {

    @Override
    protected Class<?> getDefinitionSetType() {
        return Definitions.class;
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    @Override
    public Diagram<Graph, Metadata> build(String name, Metadata metadata, Graph<DefinitionSet, ?> graph) {
        return new DiagramImpl(name,
                               graph,
                               metadata);
    }
}
